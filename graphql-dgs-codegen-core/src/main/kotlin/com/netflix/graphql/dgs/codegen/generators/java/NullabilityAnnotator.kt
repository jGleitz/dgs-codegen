/*
 *
 *  Copyright 2020 Netflix, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.netflix.graphql.dgs.codegen.generators.java

import com.netflix.graphql.dgs.codegen.CodeGenConfig
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName

interface NullabilityAnnotator {
    fun annotateNonNull(typeName: TypeName): TypeName
    fun annotateNullable(typeName: TypeName): TypeName
    fun removeNullabilityAnnotation(typeName: TypeName): TypeName
    fun isNullable(typeName: TypeName): Boolean

    companion object {
        fun of(config: CodeGenConfig) = when (config.javaNullabilityAnnotations) {
            null -> NoopAnnotator()
            "jspecify" -> JSpecifyAnnotator()
            else -> throw IllegalArgumentException("Unknown nullability library: " + config.javaNullabilityAnnotations)
        }
    }
}

class NoopAnnotator : NullabilityAnnotator {
    override fun annotateNonNull(typeName: TypeName) = typeName
    override fun annotateNullable(typeName: TypeName) = typeName
    override fun removeNullabilityAnnotation(typeName: TypeName) = typeName
    override fun isNullable(typeName: TypeName) = true
}

class JSpecifyAnnotator : NullabilityAnnotator {
    companion object {
        private val annotationsPackage = "org.jspecify.annotations"
        private val Nullable = ClassName.get(annotationsPackage, "Nullable")
        private val NonNull = ClassName.get(annotationsPackage, "NonNull")
    }

    private fun TypeName.removePreexistingNullabilityAnnotations() =
        withoutAnnotations().annotated(
            annotations.filter {
                it.type != Nullable && it.type != NonNull
            }
        )

    override fun removeNullabilityAnnotation(typeName: TypeName) = typeName.removePreexistingNullabilityAnnotations()

    override fun annotateNonNull(typeName: TypeName) = typeName.annotateWith(NonNull)

    override fun annotateNullable(typeName: TypeName) = typeName.annotateWith(Nullable)

    private fun TypeName.annotateWith(annotation: ClassName) =
        if (isPrimitive) this
        else {
            removePreexistingNullabilityAnnotations()
                .annotated(AnnotationSpec.builder(annotation).build())
        }

    override fun isNullable(typeName: TypeName) = when {
        typeName.annotations.any { it.type == NonNull } -> false
        typeName.annotations.any { it.type == Nullable } -> true
        else -> throw IllegalStateException("Type $typeName is missing a nullability annotation")
    }
}
