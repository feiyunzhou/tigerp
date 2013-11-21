package com.rex.crm;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;

import com.google.common.collect.Lists;
import com.rex.crm.admin.AdminTemplatePage;
import com.rex.crm.common.Entity;
import com.rex.crm.common.Field;
import com.rex.crm.common.FilterPanel;
import com.rex.crm.common.PageableTablePanel;
import com.rex.crm.db.DAOImpl;
import com.rex.crm.util.Configuration;
import com.rexen.crm.beans.UserRole;

public class AlertPage  extends AdminTemplatePage{
	private String search_target = "";
	public AlertPage(){
		init(null,null);
	}
	public AlertPage(final Map<String,Boolean> filter,List tdata){
        init(filter,tdata);
    }
	
	public void init(final Map<String,Boolean> filter,List tdata)
	{
		Map<String, Entity> entities = Configuration.getEntityTable();
        final Entity entity = entities.get("alert");
        setPageTitle(entity.getDisplay());
        //List mapList = DAOImpl.queryEntityList(entity.getSql(), 0, 1000);
        //TODO get userId from request's session
        final String posId = ((SignIn2Session)getSession()).getPositionId();
        final int roleId = ((SignIn2Session)getSession()).getRoleId();
        
        
        Form form = new Form("form"){

            @Override
            protected void onSubmit() {

                String sql = entity.getSql();
                switch(roleId){
                 case UserRole.USER_ROLE_ADMINISTRATOR:
                     sql = entity.getSqlAdmin();
                    break;
                 case UserRole.USER_ROLE_MANAGER:
                     sql = entity.getSqlManager();
                    break;
                 case UserRole.USER_ROLE_SALES:
                     sql = entity.getSql();
                    break;
                }
                   
                search_target = (search_target==null || search_target.equalsIgnoreCase("*"))? "":search_target;
               
                //sql =  sql + " where title like '%"+search_target+"%'";
                List<Field> searchableFields = entity.getSearchableFields();
                String joint = " like '%"+search_target+"%'";
                String likequery = "";
                for(Field sf:searchableFields){
                    likequery = likequery + " OR "+ sf.getName() + joint;
                } 
                sql =  sql + " where title like '%"+search_target+"%' " + likequery;
                List datalist = null; 
                switch(roleId){
                  case UserRole.USER_ROLE_ADMINISTRATOR:
                  datalist = DAOImpl.queryEntityRelationList(sql);
                  break;
                case UserRole.USER_ROLE_MANAGER:
                    datalist = DAOImpl.queryEntityRelationList(sql,posId,posId);
                    break;
                case UserRole.USER_ROLE_SALES:
                    datalist = DAOImpl.queryEntityRelationList(sql,posId);
                }
                setResponsePage(new ActivityPage(filter,datalist));
                
            }
            
        };
        add(form);
        
        TextField search_input = new TextField("search_input", new PropertyModel(this,"search_target"));
        form.add(search_input);
        String sql = entity.getSql();
        switch(roleId){
         case UserRole.USER_ROLE_ADMINISTRATOR:
             sql = entity.getSqlAdmin();
            break;
         case UserRole.USER_ROLE_MANAGER:
             sql = entity.getSqlManager();
            break;
         case UserRole.USER_ROLE_SALES:
             sql = entity.getSql();
            break;
        }
        
        if(tdata == null || tdata.size() == 0){
          
            if(filter == null){
                
                switch(roleId){
                  case UserRole.USER_ROLE_ADMINISTRATOR:
                  tdata = DAOImpl.queryEntityRelationList(sql);
                  break;
                case UserRole.USER_ROLE_MANAGER:
                    tdata = DAOImpl.queryEntityRelationList(sql,posId,posId);
                    break;
                case UserRole.USER_ROLE_SALES:
                    tdata = DAOImpl.queryEntityRelationList(sql, posId);
                }
              
                
            }else{
                List<String> ft = Lists.newArrayList();
                for (String k : filter.keySet()) {
                    if(filter.get(k)) ft.add(k);
                }
                
                switch(roleId){
                  case UserRole.USER_ROLE_ADMINISTRATOR:
                  tdata = DAOImpl.queryEntityWithFilter(sql, entity.getFilterField(), ft);
                  break;
                case UserRole.USER_ROLE_MANAGER:
                    tdata = DAOImpl.queryEntityWithFilter(sql, entity.getFilterField(), ft,posId,posId);
                    break;
                case UserRole.USER_ROLE_SALES:
                    tdata =DAOImpl.queryEntityWithFilter(sql, entity.getFilterField(), ft, posId);
                }
                
                
            }
            
            
        }
		add(new PageableTablePanel("datalist",entity,tdata,null));
		
		
		 //for the side bar
        List<Pair<String, Map<String, Object>>> types = null;
        switch(roleId){
          case UserRole.USER_ROLE_ADMINISTRATOR:
          types =  DAOImpl.queryFilters(sql, entity.getFilterField(), entity.getFieldByName(entity.getFilterField()).getPicklist());
          break;
        case UserRole.USER_ROLE_MANAGER:
            types =  DAOImpl.queryFilters(sql, entity.getFilterField(), entity.getFieldByName(entity.getFilterField()).getPicklist(),posId,posId);
            break;
        case UserRole.USER_ROLE_SALES:
            types = DAOImpl.queryFilters(sql, entity.getFilterField(), entity.getFieldByName(entity.getFilterField()).getPicklist(), posId);
        }
       
        add(new FilterPanel("filterPanel",types ,filter,AlertPage.class));
	}
}
