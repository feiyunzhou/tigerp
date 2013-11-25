package com.rex.crm.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import javax.mail.Session;

import org.apache.log4j.Logger;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.CheckGroupSelector;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.AbstractItem;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.rex.crm.SearchCRMUserPage;
import com.rex.crm.SignIn2Session;
import com.rex.crm.beans.Account;
import com.rex.crm.beans.CRMUser;
import com.rex.crm.beans.UserInfo;
import com.rex.crm.common.tree.Node;
import com.rex.crm.db.DAOImpl;
import com.rex.crm.util.CRMUtility;
import com.rex.crm.util.Configuration;

public class AccountPositionPanel extends Panel {
    private static final long serialVersionUID = 2501105233172820074L;
    private static final Logger logger = Logger.getLogger(AccountPositionPanel.class);
    private String etId;
    private String currentEntityName;
    List<String> selectedRowIds = Lists.newArrayList();
      
    
    public AccountPositionPanel(String id,final String en,final String entityId,int level) {
        super(id);
        etId = entityId;
        currentEntityName = en;
        final int roleId = ((SignIn2Session)getSession()).getRoleId();
        final String userId = ((SignIn2Session)getSession()).getUserId();
        //team sql
        String teamSql = "";
                //for the 医院列表
              if(level == 11){
            	  teamSql="select id as rid, user_position_query.* from user_position_query where user_position_query.position_id = ?";
              } else if(level == 21){
            	  teamSql="select id as rid, user_position_query.* from user_position_query where user_position_query.manager_position_id =?";
              }else{
            	  teamSql="select id as rid, user_position_query.* from user_position_query where manager_position_id in (select id from crmuser where reportto = ?)";
              }
        

        List mapList = DAOImpl.queryEntityRelationList(teamSql, entityId);
        Entity entity=null ;
        	  entity = Configuration.getEntityByName("regionManage");
        	  add(new Label("title","区域管理 "));
        
        List<Field> fields = entity.getFields();
        final String entityName = entity.getName();
        Form form = new Form("form");
        add(form);
        if(roleId != 1){
          WebMarkupContainer con = new WebMarkupContainer("remove_team_member_click");
            add(con);
            con.setVisible(false);
            //con.add(new AttributeAppender("style", new Model("display:none;"), ";"));
       
        }else{
        
           add(new SubmitLink("remove_team_member_click",form){
           @Override      
           public void onSubmit(){
             String teamtable = "";
               teamtable = "accountcrmuser";
            
           for(String rid:selectedRowIds){
             try{
                   DAOImpl.removeEntityFromTeam(teamtable,rid);
                
             }catch(Exception e){
                 
             }
         }
           setResponsePage(new EntityDetailPage(currentEntityName,etId));
      
             }
           });
       }
  
        //add button submission
        if(roleId != 1){
        	 WebMarkupContainer con = new WebMarkupContainer("add_users_link");
             add(con);
             con.setVisible(false);
        }else{
        	add(new Link<Void>("add_users_link"){

                @Override
                public void onClick() {
                  this.setResponsePage(new SearchCRMUserPage(currentEntityName,entityId,userId,0));
                }
                
            });

        }
        CheckGroup group = new CheckGroup("group",new PropertyModel(this,"selectedRowIds"));
        form.add(group); 
        if(roleId == 1){
            CheckGroupSelector chks = new CheckGroupSelector("checkboxs");
            group.add(chks);
            WebMarkupContainer container_label = new WebMarkupContainer("checkboxs_label");
            group.add(container_label);
            container_label.add(new AttributeAppender("for", new Model(chks.getMarkupId()), " ")); 
        }else{
            WebMarkupContainer container = new WebMarkupContainer("checkboxs");
            container.setVisible(false);
            WebMarkupContainer container_label = new WebMarkupContainer("checkboxs_label");
            container_label.setVisible(false);
            group.add(container);
            group.add(container_label);
        }
        //set column name
        RepeatingView columnNameRepeater = new RepeatingView("columnNameRepeater");
        group.add(columnNameRepeater);
        int count=0;
        for(Field f:entity.getFields()){
            if (!f.isVisible()|| f.getPriority() >1)
                continue;
            AbstractItem item = new AbstractItem(columnNameRepeater.newChildId());
            if(count==0){
                item.add(new AttributeAppender("class", new Model("table-first-link"), " "));
                count++;
            }
            columnNameRepeater.add(item);
            item.add(new Label("columnName", f.getDisplay())); 
        }
        RepeatingView dataRowRepeater = new RepeatingView("dataRowRepeater");
        group.add(dataRowRepeater);
        for (int i = 0; i < mapList.size(); i++)
        {
            Map map = (Map)mapList.get(i);
            final String rowId =  String.valueOf(map.get("rid"));     
            final String realId =  String.valueOf(map.get("id"));  
            AbstractItem item = new AbstractItem(dataRowRepeater.newChildId());
            dataRowRepeater.add(item);
            RepeatingView columnRepeater = new RepeatingView("columnRepeater");
            item.add(columnRepeater);
            for (Field f : fields) {
                if (!f.isVisible() || f.getPriority() >1)
                    continue;
                AbstractItem columnitem = new AbstractItem(columnRepeater.newChildId(), new Model() {
                    @Override
                    public Serializable getObject() {
                        Param p = new Param();
                        p.setId(rowId);
                        p.setExtraId(realId);
                        p.setEntityName(entityName);
                        return p;
                    }
                });
                if (f.isDetailLink()) {
                    String value = CRMUtility.formatValue(f.getFormatter(), String.valueOf(map.get(f.getName())));
                    if(value.equals("null")||value.equals("")||value.equals("dummy")){
                      value = "无";
                    }
                    columnitem.add(new AttributeAppender("class", new Model("table-first-link"), " "));
                    columnitem.add(new DetailLinkFragment("celldata","detailFragment",this,value));
                } else {
                    if (f.getPicklist() != null) {
                        // get option from picklist
                        String value = CRMUtility.formatValue(f.getFormatter(), DAOImpl.queryPickListByIdCached(f.getPicklist(), String.valueOf(map.get(f.getName()))));
                        if(value.equals("null")||value.equals("")||value.equals("dummy")){
                          value = "无";
                        }
                        columnitem.add(new Label("celldata", value));
                    } else if(f.getRelationTable() != null){
                        String value = CRMUtility.formatValue(f.getFormatter(), DAOImpl.queryCachedRelationDataById(f.getRelationTable(), String.valueOf(map.get(f.getName()))));
                        if(value.equals("null")||value.equals("")||value.equals("dummy")){
                          value = "无";
                        }
                        columnitem.add(new Label("celldata", value));
                    }else {
                        String value = CRMUtility.formatValue(f.getFormatter(), String.valueOf(map.get(f.getName())));
                        if(value.equals("null")||value.equals("")||value.equals("dummy")){
                          value = "无";
                        }
                        columnitem.add(new Label("celldata", value));
                    }
                }
                columnRepeater.add(columnitem);
              }
            
            WebMarkupContainer container_label = new WebMarkupContainer("checkbox_label");
            item.add(container_label);
            if(roleId == 1){
                Check chk = new Check("checkbox", new Model(String.valueOf(rowId)));
                container_label.add(new AttributeAppender("for", new Model(chk.getMarkupId()), " "));        
                item.add(chk);
            }else{
                WebMarkupContainer container = new WebMarkupContainer("checkbox");
                container.setVisible(false);
                item.add(container);
            }
         
            
        }
        add(new NewDataFormPanel("formPanel",entity,null));
    }

    public AccountPositionPanel(String id, IModel<?> model) {
        super(id, model);
    }
    
    private class DetailLinkFragment extends Fragment
    {
        public DetailLinkFragment(String id, String markupId, MarkupContainer markupProvider,String caption)
        {
            super(id, markupId, markupProvider);
            add(new Link("detailclick")
            {
                
                @Override
                public void onClick()
                {
                    Param p = (Param)getParent().getParent().getDefaultModelObject();
                    logger.debug(p+ " id:"+p.getId() + " name:"+p.getEntityName());
                    setResponsePage(new EntityDetailPage(p.getEntityName(),p.getExtraId()));
                    
                    //setResponsePage(new AccountDetailPage(id));
                }
            }.add(new Label("caption", new Model<String>(caption))));
        }
    }
    
    private class checkboxFragment extends Fragment {

      public checkboxFragment(String id, String markupId, MarkupContainer markupProvider,Model Imodel)
      {
        super(id, markupId, markupProvider);
        // TODO Auto-generated constructor stub
        add(new Check("checkbox",Imodel));
      }
      
    }
    
    private int queryPositionLevel(int positionId)
    {
    	return 0;
    }
    
    private void deleteAccountTeam(int positionId)
    {
    	
    }
    
    private void delete(int positionId)
    {
    	
    	ArrayList<Integer> ids = new ArrayList<>();
    	int level = queryPositionLevel(positionId);
    	
    	if(level == 1)
    	{
    		
    	}
    }
}
