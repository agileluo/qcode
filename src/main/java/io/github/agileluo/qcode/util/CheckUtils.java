package io.github.agileluo.qcode.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.regex.Pattern;

public class CheckUtils {
	private static final Pattern Time_Millis_Pattern = Pattern.compile("^\\d{1,}$");
	private static final Pattern Money_Pattern = Pattern.compile("^\\d{1,19}(.\\d{1,2})?$");
	private static Logger logger = LoggerFactory.getLogger(CheckUtils.class);

	/**
	 * 根据自定义注解校验<br/>
	 * 字段全部为String
	 * 
	 * @param data
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public static void checkFields(Object data) {
		checkFields(data, null);
	}

	public static boolean isJavaClass(Class<?> clz) {
		return clz != null && clz.getClassLoader() == null;
	}

	public static void checkFields(Object data, String parentName) {
		if (data == null) {
			return;
		}
		Field[] fields = data.getClass().getDeclaredFields();
		for (Field f : fields) {
			NotEmpty notEmpty = f.getDeclaredAnnotation(NotEmpty.class);
			f.setAccessible(true);
			Object vObj;
			try {
				vObj = f.get(data);
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage(), e);
			}
			String fieldName = f.getName();
			if (parentName != null) {
				fieldName = parentName + "." + fieldName;
			}
			if (vObj == null) {
				if (notEmpty != null) {
					throw new ReqParamIllegalException(fieldName, NotEmpty.MSG);
				}
			} else {
				if (vObj instanceof Collection) {
					Collection list = (Collection) vObj;
					if (notEmpty != null) {
						if (list.size() == 0) {
							throw new ReqParamIllegalException(fieldName, NotEmpty.MSG);
						}
					}
					for (Object o : list) {
						if (!isJavaClass(o.getClass())) {
							checkFields(o, fieldName);
						}
					}
				} else if (vObj instanceof Enum){
					// TODO
				} else if (vObj.getClass() == String.class) {
					String v = (String) vObj;
					if (notEmpty != null) {
						if (v.length() == 0 || v.trim().length() == 0) {
							throw new ReqParamIllegalException(fieldName, NotEmpty.MSG);
						}
					}
				} else {
					if (!isJavaClass(vObj.getClass())) {
						checkFields(vObj, fieldName);
					}
				}
			}
		}
	}

	/**
	 * 校验非空
	 * <ol>
	 * <li>可填多个参数</li>
	 * <li>支持对象深度校验， 如 contact.name：联系人姓名不能为空</li>
	 * <li>支持数组校验， 如 children[]：数组不能为空</li>
	 * <li>支持数组深度校验， 如 children[].name： 数据的名称不能为空</li>
	 * </ol>
	 * 
	 * @param o
	 * @param fields
	 */
	public static void checkEmpty(Object o, String... fields) {
		for (String field : fields) {
			Object value = getValue(o, field);
			if (value == null) {
				throw new ParamEmptyException(field);
			}
			if (value instanceof ResultList) {
				((ResultList) value).check(field);
			} else if (isEmpty(value)) {
				throw new ParamEmptyException(field);
			}
		}
	}

	/**
	 * 校验非空
	 * @param key
	 * @param value
	 */
	public static void checkEmpty(String key, Object value){
	  if(isEmpty(value)){
	  	throw new ParamEmptyException(key);
	  }
	}

	/**
	 * 获取属性
	 * 
	 * @param o
	 * @param field
	 * @return
	 */
	static Object getValue(Object o, String field) {
		if (o == null || field == null || field.length() == 0) {
			return null;
		}
		Object deapObj = o;
		String deapField = field;

		String[] fields = field.split("\\.");
		if (fields.length > 1) {
			for (String f : fields) {
				deapObj = getValue(deapObj, f);
				if (deapObj == null) {
					return null;
				}
			}
			return deapObj;
		}

		if ("[]".equals(field)) {
			return o;
		}

		boolean isArray = field.contains("[]");
		if (isArray) {
			deapField = field.substring(0, field.length() - 2);
		}
		String upField = deapField.substring(0, 1).toUpperCase() + deapField.substring(1);

		ResultList result = new ResultList();
		if (deapObj.getClass() == ResultList.class) {
			deapObj = ((ResultList) deapObj).getResult();
		}
		if (deapObj instanceof Collection) {
			Collection<?> list = (Collection<?>) deapObj;
			for (Object l : list) {
				Object value = getValue(l, upField);
				result.add(value);
			}
			return result;
		} else if (deapObj.getClass().isArray()) {
			int length = Array.getLength(deapObj);
			for (int i = 0; i < length; i++) {
				Object value = getValue(Array.get(deapObj, i), upField);
				result.add(value);
			}
		}

		Method m = null;
		Class<?> c = deapObj.getClass();
		try {
			m = c.getDeclaredMethod("get" + upField);
		} catch (NoSuchMethodException e) {
			try {
				m = c.getDeclaredMethod("is" + upField);
			} catch (NoSuchMethodException e1) {
			} catch (SecurityException e1) {
			}
		} catch (SecurityException e) {
		}
		if (m != null) {
			try {
				m.setAccessible(true);
				return m.invoke(deapObj);
			} catch (Exception e) {
			}
		}
		return null;
	}

	static boolean isEmpty(Object value) {
		if (value != null) {
			if (value instanceof String) {
				String str = (String) value;
				if (!(str.length() == 0 || str.trim().length() == 0)) {
					return false;
				}
			} else if (value.getClass().isArray()) {
				if (Array.getLength(value) > 0) {
					return false;
				}
			} else if (value instanceof Collection) {
				@SuppressWarnings("rawtypes")
				Collection list = (Collection) value;
				if (list.size() > 0) {
					return false;
				}
			} else {
				return false;
			}
		}
		return true;
	}
}

