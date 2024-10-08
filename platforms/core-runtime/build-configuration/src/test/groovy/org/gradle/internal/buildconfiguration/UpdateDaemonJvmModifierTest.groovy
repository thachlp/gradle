/*
 * Copyright 2024 the original author or authors.
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

package org.gradle.internal.buildconfiguration

import org.gradle.internal.buildconfiguration.tasks.UpdateDaemonJvmModifier
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmImplementation
import org.gradle.test.fixtures.file.TestFile
import org.gradle.test.fixtures.file.TestNameTestDirectoryProvider
import org.junit.Rule
import spock.lang.Specification

class UpdateDaemonJvmModifierTest extends Specification {
    @Rule
    final TestNameTestDirectoryProvider tmpDir = new TestNameTestDirectoryProvider(getClass())

    final TestFile daemonJvmPropertiesFile = tmpDir.file(DaemonJvmPropertiesDefaults.DAEMON_JVM_PROPERTIES_FILE)

    def "writes expected properties into file"() {
        when:
        UpdateDaemonJvmModifier.updateJvmCriteria(daemonJvmPropertiesFile, JavaLanguageVersion.of(11), "IBM", JvmImplementation.VENDOR_SPECIFIC)
        then:
        def props = daemonJvmPropertiesFile.properties
        props[DaemonJvmPropertiesDefaults.TOOLCHAIN_VERSION_PROPERTY] == "11"
        props[DaemonJvmPropertiesDefaults.TOOLCHAIN_VENDOR_PROPERTY] == "IBM"
        props[DaemonJvmPropertiesDefaults.TOOLCHAIN_IMPLEMENTATION_PROPERTY] == "vendor-specific"
        daemonJvmPropertiesFile.text.contains("#This file is generated by " + DaemonJvmPropertiesConfigurator.TASK_NAME)
    }

    def "writes only non-null properties into file"() {
        when:
        UpdateDaemonJvmModifier.updateJvmCriteria(daemonJvmPropertiesFile, JavaLanguageVersion.of(11), null, JvmImplementation.VENDOR_SPECIFIC)
        then:
        def props = daemonJvmPropertiesFile.properties
        props[DaemonJvmPropertiesDefaults.TOOLCHAIN_VERSION_PROPERTY] == "11"
        props[DaemonJvmPropertiesDefaults.TOOLCHAIN_VENDOR_PROPERTY] == null
        props[DaemonJvmPropertiesDefaults.TOOLCHAIN_IMPLEMENTATION_PROPERTY] == "vendor-specific"
    }

    def "writes only java version when no other properties are given"() {
        when:
        UpdateDaemonJvmModifier.updateJvmCriteria(daemonJvmPropertiesFile, JavaLanguageVersion.of(11), null, null)
        then:
        def props = daemonJvmPropertiesFile.properties
        props[DaemonJvmPropertiesDefaults.TOOLCHAIN_VERSION_PROPERTY] == "11"
        props[DaemonJvmPropertiesDefaults.TOOLCHAIN_VENDOR_PROPERTY] == null
        props[DaemonJvmPropertiesDefaults.TOOLCHAIN_IMPLEMENTATION_PROPERTY] == null
    }

    def "existing properties are removed when null is passed"() {
        given:
        daemonJvmPropertiesFile.text = """
            ${DaemonJvmPropertiesDefaults.TOOLCHAIN_VERSION_PROPERTY}=11
            ${DaemonJvmPropertiesDefaults.TOOLCHAIN_VENDOR_PROPERTY}=IBM
            ${DaemonJvmPropertiesDefaults.TOOLCHAIN_IMPLEMENTATION_PROPERTY}=vendor-specific
        """
        when:
        UpdateDaemonJvmModifier.updateJvmCriteria(daemonJvmPropertiesFile, JavaLanguageVersion.of(15), null, null)
        then:
        def props = daemonJvmPropertiesFile.properties
        props[DaemonJvmPropertiesDefaults.TOOLCHAIN_VERSION_PROPERTY] == "15"
        props[DaemonJvmPropertiesDefaults.TOOLCHAIN_VENDOR_PROPERTY] == null
        props[DaemonJvmPropertiesDefaults.TOOLCHAIN_IMPLEMENTATION_PROPERTY] == null
    }

    def "existing unrecognized properties are not preserved"() {
        daemonJvmPropertiesFile.text = """
            # this comment is not preserved
            com.example.foo=bar
            ${DaemonJvmPropertiesDefaults.TOOLCHAIN_VERSION_PROPERTY}=15
        """
        when:
        UpdateDaemonJvmModifier.updateJvmCriteria(daemonJvmPropertiesFile, JavaLanguageVersion.of(11), "IBM", JvmImplementation.VENDOR_SPECIFIC)
        then:
        def props = daemonJvmPropertiesFile.properties
        props.size() == 3
        props[DaemonJvmPropertiesDefaults.TOOLCHAIN_VERSION_PROPERTY] == "11"
        props[DaemonJvmPropertiesDefaults.TOOLCHAIN_VENDOR_PROPERTY] == "IBM"
        props[DaemonJvmPropertiesDefaults.TOOLCHAIN_IMPLEMENTATION_PROPERTY] == "vendor-specific"
        !daemonJvmPropertiesFile.text.contains("# this comment is not preserved")
    }
}
