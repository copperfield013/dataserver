package cn.sowell.dataserver.model.karuiserv.jsonresolver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.sowell.datacenter.entityResolver.EntityConstants;
import cn.sowell.datacenter.entityResolver.ModuleEntityPropertyParser;
import cn.sowell.datacenter.entityResolver.impl.ArrayItemPropertyParser;
import cn.sowell.dataserver.Constants;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailField;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailFieldGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailTemplate;

/**
 *  用于将entity根据轻服务提供的json格式元数据，转换成对应的json字符串
 * @author CopperfieldZhang
 *
 */
public class ResponseJsonMetaResolver {
	
	private EntityJsonMeta meta;
	private Map<Long, TemplateDetailFieldGroup> fieldGroupMap = new HashMap<Long, TemplateDetailFieldGroup>();
	private Map<Long, TemplateDetailField> fieldMap = new HashMap<Long, TemplateDetailField>();
	
	
	
	
	public ResponseJsonMetaResolver(EntityJsonMeta meta, TemplateDetailTemplate dtmpl) {
		super();
		Assert.notNull(meta);
		Assert.notNull(dtmpl);
		this.meta = meta;
		for (TemplateDetailFieldGroup fieldGroup : dtmpl.getGroups()) {
			this.fieldGroupMap.put(fieldGroup.getId(), fieldGroup);
			for (TemplateDetailField field : fieldGroup.getFields()) {
				if(field.getFieldAvailable()) {
					this.fieldMap.put(field.getId(), field);
				}
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	public JSONObject resolve(ModuleEntityPropertyParser parser) {
		JSONObject jEntity = new JSONObject();
		//读取meta，遍历所有字段
		putJsonFields(jEntity, parser, meta.getFields());
		return jEntity;
	}


	private void putJsonFields(JSONObject jEntity, ModuleEntityPropertyParser parser, List<JsonMetaField> fields) {
		for (JsonMetaField field : fields) {
			if(!field.getDisabled()) {
				if(field instanceof JsonMetaProperty) {
					putJsonField(jEntity, parser, (JsonMetaProperty) field);
				}else if(field instanceof JsonMetaComposite) {
					putJsonField(jEntity, parser, (JsonMetaComposite) field);
				}
			}
		}
	}

	private void putJsonArrayItemFileds(JSONObject jArrayItem, ArrayItemPropertyParser parser, List<JsonMetaField> fields) {
		for (JsonMetaField field : fields) {
			if(!field.getDisabled()) {
				if(field instanceof JsonMetaProperty) {
					putJsonField(jArrayItem, parser, (JsonMetaProperty) field);
				}else if(field instanceof JsonMetaRelationLabel) {
					putJsonField(jArrayItem, parser, (JsonMetaRelationLabel)field);
				}
			}
		}
	}

	/**
	 * 将arrayItem的普通字段值放到jArrayItem中
	 * @param jArrayItem
	 * @param parser
	 * @param field
	 */
	private void putJsonField(JSONObject jArrayItem, ArrayItemPropertyParser parser, JsonMetaProperty field) {
		if(field.getDtmplFieldId() != null) {
			//获得详情模板中的字段
			TemplateDetailField dtmplField = getDetailTemplateField(field.getDtmplFieldId());
			jArrayItem.put(field.getName(), parser.getFormatedProperty(dtmplField.getFieldName()));
		}
	}


	/**
	 * 将arrayItem的关系名字段值放到jArrayItem中
	 * @param jArrayItem
	 * @param parser
	 * @param field
	 */
	private void putJsonField(JSONObject jArrayItem, ArrayItemPropertyParser parser, JsonMetaRelationLabel field) {
		jArrayItem.put(field.getName(), parser.getFormatedProperty(EntityConstants.LABEL_KEY));
	}


	/**
	  *  将entity中的普通字段field放到target中
	 * @param target
	 * @param entity
	 * @param field
	 */
	private void putJsonField(JSONObject target, ModuleEntityPropertyParser entity, JsonMetaProperty field) {
		if(field.getDtmplFieldId() != null) {
			//获得详情模板中的字段
			TemplateDetailField dtmplField = getDetailTemplateField(field.getDtmplFieldId());
			if(dtmplField != null) {
				String propertyValue = entity.getFormatedProperty(dtmplField.getFieldName());
				target.put(field.getName(), propertyValue);
			}
		}
	}
	
	/**
	 * 将顶级entity对象的根据的composite字段放到target中
	 * @param target
	 * @param entity
	 * @param composite
	 */
	private void putJsonField(JSONObject target, ModuleEntityPropertyParser entity, JsonMetaComposite composite) {
		if(JsonMetaComposite.TYPE_ARRAY.equals(composite.getType())) {
			if(composite.getDtmplCompositeId() != null) {
				TemplateDetailFieldGroup group = getDetailTemplateFieldGroup(composite.getDtmplCompositeId());
				if(group != null) {
					if(Constants.TRUE.equals(group.getIsArray())) {
						//如果是多值/关系的字段组
						JSONArray jArrayItems = new JSONArray();
						List<ArrayItemPropertyParser> arrayItems = entity.getCompositeArray(group.getComposite().getName());
						if(arrayItems != null) {
							for (ArrayItemPropertyParser item : arrayItems) {
								JSONObject jArrayItem = new JSONObject();
								putJsonArrayItemFileds(jArrayItem, item, composite.getFields());
								jArrayItems.add(jArrayItem);
							}
						}
						target.put(composite.getName(), jArrayItems);
					}
				}
			}
		}else if(JsonMetaComposite.TYPE_NORMAL.equals(composite.getType())) {
			//如果是普通字段的字段组
			JSONObject jNormalComposite = new JSONObject();
			putJsonFields(jNormalComposite, entity, composite.getFields());
			target.put(composite.getName(), jNormalComposite);
		}
	}

	
	private TemplateDetailField getDetailTemplateField(Long dtmplFieldId) {
		return this.fieldMap.get(dtmplFieldId);
	}

	private TemplateDetailFieldGroup getDetailTemplateFieldGroup(Long dtmplGroupId) {
		return this.fieldGroupMap.get(dtmplGroupId);
	}
}
