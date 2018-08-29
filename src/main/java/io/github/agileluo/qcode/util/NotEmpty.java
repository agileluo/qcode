package io.github.agileluo.qcode.util;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 内容不能为空
 * @author luoml
 *
 */
@Target({FIELD})
@Retention(RUNTIME)
@Documented
public @interface NotEmpty {
	String MSG = "不能为空";
}
