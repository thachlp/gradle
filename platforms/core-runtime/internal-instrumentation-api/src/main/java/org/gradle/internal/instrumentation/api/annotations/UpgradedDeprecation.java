/*
 * Copyright 2023 the original author or authors.
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

package org.gradle.internal.instrumentation.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks that a property upgrade should show deprecation
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD})
public @interface UpgradedDeprecation {

    /**
     * Set if deprecation nagging is enabled or not
     */
    boolean enabled() default true;

    int removedIn() default -1;

    int withUpgradeGuideVersion() default -1;

    String withUpgradeGuideSection() default "";
}
