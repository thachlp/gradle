/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle

import org.gradle.integtests.fixtures.AbstractIntegrationSpec
import org.gradle.integtests.fixtures.executer.GradleExecuter
import org.gradle.util.GFileUtils
import org.gradle.util.Requires
import spock.lang.Issue

import static org.gradle.util.TestPrecondition.NOT_LINUX

class NativeServicesIntegrationTest extends AbstractIntegrationSpec {

    final File nativeDir = new File(executer.gradleUserHomeDir, 'native')
    final File jansiDir = new File(nativeDir, 'jansi')

    def setup() {
        GFileUtils.deleteDirectory(nativeDir) // ensure that directory wasn't created by other tests
        requireGradleDistribution()
    }

    def "native services libs are unpacked to gradle user home dir"() {
        when:
        quietExecutor().run()

        then:
        nativeDir.directory
    }

    def "throws exception if Jansi library directory cannot be created"() {
        given:
        GFileUtils.touch(jansiDir)

        when:
        def failure = quietExecutor().runWithFailure()

        then:
        failure.error.contains("Unable to create Jansi library path '$jansiDir.absolutePath'")
    }

    def "creates Jansi library directory and reuses for succeeding Gradle invocations"() {
        when:
        quietExecutor().run()

        then:
        jansiDir.directory

        when:
        quietExecutor().run()

        then:
        jansiDir.directory
    }

    @Issue("GRADLE-3573")
    @Requires(adhoc = { NativeServicesIntegrationTest.isMountedNoexec('/tmp') })
    def "creates Jansi library directory even if tmp dir is mounted with noexec option"() {
        when:
        executer.withNoExplicitTmpDir().withBuildJvmOpts("-Djava.io.tmpdir=/tmp").run()

        then:
        jansiDir.directory
    }

    static boolean isMountedNoexec(String dir) {
        if (NOT_LINUX) {
            return false
        }

        def out = new StringBuffer()
        'mount'.execute().waitForProcessOutput(out, System.err)
        out.readLines().find { it.startsWith("tmpfs on $dir type tmpfs") && it.contains('noexec') } != null
    }

    private GradleExecuter quietExecutor() {
        executer.withArguments('-q')
    }
}
