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

package org.gradle.api.internal.artifacts.ivyservice.resolveengine.graph;

import java.util.List;

public class CompositeDependencyGraphVisitor implements DependencyGraphVisitor {
    private final List<DependencyGraphVisitor> visitors;

    public CompositeDependencyGraphVisitor(List<DependencyGraphVisitor> visitors) {
        this.visitors = visitors;
    }

    @Override
    public void start(RootGraphNode root) {
        for (DependencyGraphVisitor visitor : visitors) {
            visitor.start(root);
        }
    }

    @Override
    public void visitNode(DependencyGraphNode node) {
        for (DependencyGraphVisitor visitor : visitors) {
            visitor.visitNode(node);
        }
    }

    @Override
    public void visitEdges(DependencyGraphNode node) {
        for (DependencyGraphVisitor visitor : visitors) {
            visitor.visitEdges(node);
        }
    }

    @Override
    public void finish(RootGraphNode root) {
        for (DependencyGraphVisitor visitor : visitors) {
            visitor.finish(root);
        }
    }
}
