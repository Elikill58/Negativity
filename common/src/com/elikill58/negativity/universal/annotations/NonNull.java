package com.elikill58.negativity.universal.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If an expression's type is qualified by {@code @NonNull}, then the expression never evaluates to
 * {@code null}.
 *
 * <p>For fields of a class, the {@link NonNull} annotation indicates that this field is never
 * {@code null} <em>after the class has been fully initialized</em>. For static fields, the {@link
 * NonNull} annotation indicates that this field is never {@code null} <em>after the containing
 * class is initialized</em>
 *
 * <p>This annotation is rarely written in source code, because it is the default.
 *
 * @see Nullable
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
public @interface NonNull {}
