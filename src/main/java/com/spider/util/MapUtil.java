package com.spider.util;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes","unchecked"})
public class MapUtil {
	/**
	 * 从map中获取指定的值
	 * 
	 * @param map
	 * @param pathKey
	 * @return
	 */
	public static Object getValue(Map map, String pathKey) {
		if (map == null) {
			throw new RuntimeException("map not null");
		}
		if (pathKey == null) {
			throw new RuntimeException("pathKey not null");
		}
		//判断有无下级的key
		int doIndex=pathKey.indexOf(".");
		if (-1==doIndex) {
			//无下级key
			//判断是否是list
			int leftIndex=pathKey.indexOf("[");
			if (-1==leftIndex) {
				//非list，直接返回
				return map.get(pathKey);
			}else {
				//pathKey可能是list,判断解析
				int rightIndex=pathKey.indexOf("]",leftIndex+1);
				if (-1==rightIndex) {
					throw new RuntimeException("wrong path");
				}
				//取得list
				String realKey=pathKey.substring(0,leftIndex);
				Object listObject=map.get(realKey);
				if (null==listObject) {
					return listObject;
				}else if (!(listObject instanceof List)) {
					throw new RuntimeException("pathKey error");
				}
				List list = (List) listObject;
				//是否是[]
				if (rightIndex==leftIndex+1) {
					//[]返回list
					return list;
				}else {
					//[i] 第[i]个元素
					int index=-1;
					try {
						index=Integer.parseInt(pathKey.substring(leftIndex+1,rightIndex));
					} catch (Exception e) {
						throw new RuntimeException("pathKey error");
					}
					if (index<0) {
						throw new RuntimeException("pathKey error");
					}
					if (index>list.size()-1) {
						return null;
					}
					return list.get(index);
				}
			}
		}else {
			//有下级key
			if (doIndex==pathKey.length()-1) {
				//有 "."但没下级key
				throw new RuntimeException("pathKey error");
			}
			//取本级map
			String firstKey=pathKey.substring(0,doIndex);
			Object firstValue=getValue(map, firstKey);
			if (null==firstValue) {
				return null;
			}else if (firstValue instanceof Map) {
				String restKey=pathKey.substring(doIndex+1);
				//递归取下级数据
				return getValue((Map)firstValue, restKey);
			}else {
				throw new RuntimeException("pathKey error");
			}
		}
	}
	
	/**
	 * 按路径往map中放值，前提是当前map不存在该键值
	 * @param map
	 * @param pathKey
	 * @param value
	 */
	public static void setValue(Map map,String pathKey,Object value){
		if (map == null) {
			throw new RuntimeException("map not null");
		}
		if (pathKey == null) {
			throw new RuntimeException("pathKey not null");
		}
		//判断有无下级的key
		int doIndex=pathKey.indexOf(".");
		if (-1==doIndex) {
			//无下级key
			//判断是否是list
			int leftIndex=pathKey.indexOf("[");
			if (-1==leftIndex) {
				//非list，直接返回
				map.put(pathKey,value);
				return;
			}else {
				//pathKey可能是list,判断解析
				int rightIndex=pathKey.indexOf("]",leftIndex+1);
				if (-1==rightIndex) {
					throw new RuntimeException("pathKey error");
				}
				//是否[]
				boolean keyIsArray=false;
				if (rightIndex==leftIndex+1) {
					keyIsArray=true;
					//key为整个list表达式，则待设置的值是list
					if (value!=null&&!(value instanceof List)) {
						throw new RuntimeException("pathKey error");
					}
				}
				//取得list
				String realKey=pathKey.substring(0,leftIndex);
				Object listObject=map.get(realKey);
				if (null==listObject) {
					if (keyIsArray) {
						map.put(realKey, value);
						return;
					}else {
						listObject=new ArrayList();
						map.put(realKey,listObject);
					}
				}else if (!(listObject instanceof List)) {
					throw new RuntimeException("pathKey error");
				}
				List list =(List) listObject;
				//是否[]
				if (keyIsArray) {
					map.put(realKey,value);
					return;
				}else {
					//[i]设置第i个元素
					int index=-1;
					try {
						index=Integer.parseInt(pathKey.substring(leftIndex+1,rightIndex));
					} catch (Exception e) {
						throw new RuntimeException("pathKey error");
					}
					if (index<0) {
						throw new RuntimeException("pathKey error");
					}
					//补全list
					if (index>=list.size()) {
						for (int i = list.size(); i < index+1; i++) {
							list.add(null);
						}
					}
					list.set(index, value);
				}
			}
		}else {
			//有下级key
			if (doIndex==pathKey.length()-1) {
				//有 "."但没下级key
				throw new RuntimeException("pathKey error");
			}
			//取本级map
			String firstKey=pathKey.substring(0,doIndex);
			if (firstKey.indexOf("[]")!=-1) {
				throw new RuntimeException("pathKey error");
			}
			Object firstValue=getValue(map, firstKey);
			Map firstMap=null;
			if (firstValue==null) {
				firstMap=new HashMap();
				setValue(map, firstKey, firstMap);
			}else if (firstValue instanceof Map) {
				firstMap=(Map) firstValue;
			}else {
				throw new RuntimeException("pathKey error");
			}
			String restKey=pathKey.substring(doIndex+1);
			//递归设置下级数据
			setValue(firstMap, restKey, value);
		}
	}
			
}
