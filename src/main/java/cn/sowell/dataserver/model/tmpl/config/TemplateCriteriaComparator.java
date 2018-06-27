package cn.sowell.dataserver.model.tmpl.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.util.TriConsumer;

import com.abc.application.FusionContext;
import com.abc.query.criteria.Criteria;
import com.abc.query.criteria.CriteriaFactory;


public class TemplateCriteriaComparator {
	private String key;
	private TriConsumer<CriteriaFactory, List<Criteria>, NormalCriteria> consumer; 
	private FusionContext context;
	private CriteriaFactory criteriaFactory;
	
	public TemplateCriteriaComparator(String key, TriConsumer<CriteriaFactory, List<Criteria>, NormalCriteria> consumer) {
		this.key = key;
		this.consumer = consumer;
		criteriaFactory = new CriteriaFactory(context);
	}
	
	public String getKey() {
		return key;
	}
	
	public List<Criteria> handler(NormalCriteria criteria){
		List<Criteria> criterias = new ArrayList<Criteria>();
		consumer.accept(criteriaFactory, criterias, criteria);
		return criterias;
	}
	
	
	
	
	private static Map<String, TemplateCriteriaComparator> comparatorMap = new HashMap<String, TemplateCriteriaComparator>();
	static{
		comparatorMap.put("", new TemplateCriteriaComparator("s1", (cf, cs, criteria)->{
			cf.createLikeQueryCriteria(criteria.getAttributeName(), criteria.getValue());
		}));
	}
	public static TemplateCriteriaComparator getComparator(String key){
		return comparatorMap.get(key);
	}

	
	
}
