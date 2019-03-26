package cn.sowell.dataserver.model.tmpl.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.sowell.dataserver.model.cachable.dao.CachableDao;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateTreeNode;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateTreeNodeCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateTreeRelation;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateTreeRelationCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateTreeTemplate;

public interface TreeTemplateDao extends CachableDao<TemplateTreeTemplate> {

	List<TemplateTreeNode> queryAllNodes();

	List<TemplateTreeNodeCriteria> queryAllNodeCriterias();
	
	List<TemplateTreeRelation> queryAllRelation();

	List<TemplateTreeRelationCriteria> queryAllCriterias();

	List<TemplateTreeNode> queryNodeList(Long ttmplId);
	
	Map<Long, List<TemplateTreeRelation>> queryRelationsMapByNodes(Set<Long> nodeIds);
	
	Map<Long, List<TemplateTreeRelationCriteria>> queryCriteriasMapByRelations(Set<Long> nodeRelationIds);

	Map<Long, List<TemplateTreeNodeCriteria>> queryNodeCriterias(Set<Long> nodeIds);


	

	

}
