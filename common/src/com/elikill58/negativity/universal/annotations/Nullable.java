package com.elikill58.negativity.universal.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link Nullable} is a type annotation that indicates that the value is not known to be non-null
 * (see {@link NonNull}). Only if an expression has a {@link Nullable} type may it be assigned
 * {@code null}.
 *
 * @see NonNull
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
public @interface Nullable {}
