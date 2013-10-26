package com.rex.crm.common;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.PopupSettings;
import org.apache.wicket.markup.html.list.AbstractItem;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.rex.crm.PageFactory;
import com.rex.crm.SelectEntryPage;
import com.rex.crm.SignIn2Session;
import com.rex.crm.TemplatePage;
import com.rex.crm.beans.Choice;
import com.rex.crm.db.DAOImpl;
import com.rex.crm.util.CRMUtility;
import com.rex.crm.util.Configuration;

public class EditDataFormPanel extends Panel {
	private static final Logger logger = Logger.getLogger(EditDataFormPanel.class);
	private static final long serialVersionUID = -2613412283023068638L;
	private final String user = ((SignIn2Session) getSession()).getUser();
	private Map<String, List<Field>> fieldGroupMap = Maps.newHashMap(); 

	private static int NUM_OF_COLUMN = 1;
	
    private Map<String, IModel> parentModels = Maps.newHashMap();
    private Map<String, DropDownChoiceFragment> childDropDownComponent = Maps.newHashMap();

	/**
	 * 
	 * @param id
	 * @param schema
	 * @param data
	 * @param entityId
	 * @param relationIds
	 */
	public EditDataFormPanel(String id, final Entity schema,  Map data,final String entityId ) {
		super(id);
		//add prompt 
        final RepeatingView div = new RepeatingView("promptDiv");
        final AbstractItem divitem = new AbstractItem(div.newChildId());
        final Label promptButton = new Label("promptButton","X");
        divitem.add(promptButton);
        final Label promptLabel = new Label("prompt","红色输入框是必填字段不能为空，请输入!");
        divitem.add(promptLabel);
        div.add(new AttributeAppender("style",new Model("display:none"),";"));
        divitem.add(new AttributeAppender("style",new Model("display:none"),";"));
        div.add(divitem);
        add(div);	
        if(schema.getName().equals("activity")){
      	   add(new Label("name",String.valueOf(data.get("title"))));
         }else{
      	   add(new Label("name",String.valueOf(data.get("name"))));
  	   }
		final Map<String, IModel> models = Maps.newHashMap();
		final Map<String, IModel> fieldNameToModel = Maps.newHashMap();
		final String userId = ((SignIn2Session) getSession()).getUserId();
		String primaryKeyName = schema.getPrimaryKeyName();
		List<Field> fields = schema.getFields();// 得到所有fields
		final List<String> fieldNames = Lists.newArrayList();
		// List<String> fn = schema.getFieldNames();
		for (Field f : fields) {
			if (fieldGroupMap.get(f.getFieldGroup()) != null) {
				fieldGroupMap.get(f.getFieldGroup()).add(f);// 以fieldgroup为条件查询，并将其添加入新的集合中
			} else {
				List<Field> fs = Lists.newArrayList();
				fs.add(f);
				fieldGroupMap.put(f.getFieldGroup(), fs);
			}
		}
		List<String> groupNames = Configuration.getSortedFieldGroupNames();// 得到分组信息
		RepeatingView fieldGroupRepeater = new RepeatingView("fieldGroupRepeater");
		add(fieldGroupRepeater);
		for (String gn : groupNames) {
			logger.info("gn = "+gn+ "groupNames = "+groupNames);

			List<Field> groupfields = fieldGroupMap.get(gn);
			if (groupfields == null)
				continue;
			AbstractItem groupitem = new AbstractItem(fieldGroupRepeater.newChildId());
			fieldGroupRepeater.add(groupitem);
			RepeatingView dataRowRepeater = new RepeatingView("dataRowRepeater");
			groupitem.add(dataRowRepeater);
			int numOfField = 0;
			List<Field> visibleFields = Lists.newArrayList();
			for (Field f : groupfields) {
				if (!f.isVisible() )
					continue;
				numOfField++;
				visibleFields.add(f);
			}
				groupitem.add(new Label("groupname", gn));	
			
			int num_of_row = (numOfField / NUM_OF_COLUMN) + 1;
			
			for (int i = 0; i < num_of_row; i++) {
				logger.debug("for i = "+i+",dataRowRepeater.newChildId() = "+dataRowRepeater.newChildId()+",value = "+dataRowRepeater.get(dataRowRepeater.newChildId()));
				AbstractItem item = new AbstractItem(dataRowRepeater.newChildId());
				dataRowRepeater.add(item);
				RepeatingView columnRepeater = new RepeatingView("columnRepeater");
				item.add(columnRepeater);

				for (int j = 0; j < 2 * NUM_OF_COLUMN; j++) {

					AbstractItem columnitem = new AbstractItem(columnRepeater.newChildId(), new Model(String.valueOf(data.get(primaryKeyName))));

					if ((i * NUM_OF_COLUMN + j / 2) >= visibleFields.size()) {
						if((i * NUM_OF_COLUMN + j / 2) >= visibleFields.size()){
							continue;
						}
						columnitem.add(new LayoutFragment("editdata","layoutFragment", this, "&nbsp;"));
						columnRepeater.add(columnitem);
						continue;
					}
					final Field currentField = visibleFields.get(i * NUM_OF_COLUMN + j / 2);
					if (currentField.getPicklist() != null) {
						if (j % 2 == 0) {
						  if (currentField.isRequired()) {
                columnitem.add(new TextFragment("editdata", "textFragment", this, currentField.getDisplay() + ":").add(new AttributeAppender("style", new Model("font-weight:bold;"), ";")));
                columnitem.add(new AttributeAppender("class", new Model("tag"), " ")).add(new AttributeAppender("style", new Model("color:red"), ";"));
                fieldNames.add(currentField.getName());
						  }else{
                columnitem.add(new TextFragment("editdata", "textFragment", this, currentField.getDisplay() + ":").add(new AttributeAppender("style", new Model("font-weight:bold"), ";")));
                columnitem.add(new AttributeAppender("class", new Model("tag"), " "));
                fieldNames.add(currentField.getName());
              }
						} else {
   
						    if (currentField.getParentNode() != null || currentField.getChildNode() != null) {
						        String value = data.get(currentField.getName()).toString();
						        IModel<Choice> selected_model =  new Model(new Choice(Long.parseLong(value),""));
                                IModel<List<? extends Choice>> choices_models = null;
                                
                                if(currentField.getChildNode()!=null && currentField.getParentNode() == null ){
                                    choices_models = Model.ofList(DAOImpl.queryPickList(currentField.getPicklist()));
                                  }
                                
                                if(currentField.getParentNode()!=null){
                                    choices_models = new AbstractReadOnlyModel<List<? extends Choice>>(){
                                        List<Choice> choices;

                                        @Override
                                        public List<? extends Choice> getObject() {
                                            IModel pm = parentModels.get(currentField.getParentNode());
                                            if(pm!=null){
                                              choices =  DAOImpl.queryPickListByFilter(currentField.getPicklist(), "parentId",String.valueOf(((Choice)pm.getObject()).getId()));
                                             
                                            }else{
                                                choices = Lists.newArrayList();
                                            }
                                            return choices;
                                        }
                                        
                                        @Override
                                        public void detach() {
                                            choices = null;
                                            //System.out.println("detached");
                                        }
   
                                      };
                                }
                                
                                
                                DropDownChoiceFragment  selector = new DropDownChoiceFragment("editdata", "dropDownFragment", this, choices_models, selected_model,schema,currentField);
                                columnitem.add(selector);
                                selector.setOutputMarkupId(true);
                               
                                if(currentField.getParentNode()!=null){
                                   
                                   childDropDownComponent.put(currentField.getName(),selector);
                                }
                                
                                models.put(currentField.getName(), selected_model);
                                fieldNameToModel.put(currentField.getName(), selected_model);
                                columnitem.add(selector);
						        
						    }else{
						    
							List<Choice> pickList = DAOImpl.queryPickList(currentField.getPicklist());
							Map<Long, String> list = Maps.newHashMap();
							List<Long> ids = Lists.newArrayList();
							for (Choice p : pickList) {
								list.put(p.getId(), p.getVal());
								ids.add(p.getId());
							}
							String value = data.get(currentField.getName()).toString();
							IModel choiceModel =  new Model(Long.parseLong(value));
					
							models.put(currentField.getName(), choiceModel);
							
							fieldNameToModel.put(currentField.getName(), choiceModel);
							columnitem.add(new DropDownChoiceFragment("editdata", "dropDownFragment", this, ids,list, choiceModel));
						    }
						}
					}
					else if (currentField.getRelationTable() != null) {
						if (j % 2 == 0) {
						  if (currentField.isRequired()) {
                columnitem.add(new TextFragment("editdata", "textFragment", this, currentField.getDisplay() + ":").add(new AttributeAppender("style", new Model("font-weight:bold;"), ";")));
                columnitem.add(new AttributeAppender("class", new Model("tag"), " ")).add(new AttributeAppender("style", new Model("color:red"), ";"));
                fieldNames.add(currentField.getName());
              }else{
                columnitem.add(new TextFragment("editdata", "textFragment", this, currentField.getDisplay() + ":").add(new AttributeAppender("style", new Model("font-weight:bold"), ";")));
                columnitem.add(new AttributeAppender("class", new Model("tag"), " "));
                fieldNames.add(currentField.getName());
              }
						} else {
						    long foreignKey = 1L;
                            foreignKey = ((Number)data.get(currentField.getName())).longValue();
							IModel choiceModel = new Model(foreignKey);
							String fn = "";
							if (currentField.getAlias() != null) {
								fn = currentField.getAlias();
							} else {
								fn = currentField.getName();
							}
							final Entity ent = Configuration.getEntityByName(currentField.getRelationTable());
							Map et = DAOImpl.queryEntityById(ent.getSql_ent(), String.valueOf(foreignKey));
							String value = "";
							if(et != null && et.get("name") != null){
							     value =  (String)et.get("name");
							}
							models.put(fn, choiceModel);
							
							fieldNameToModel.put(fn, choiceModel);
						    columnitem.add(new RelationTableSearchFragment("editdata","relationTableSearchFragment",this,currentField.getRelationTable(),value,choiceModel,entityId));
						}
					} 
						else {
						if (j % 2 == 0) {
						  if (currentField.isRequired()) {
                columnitem.add(new TextFragment("editdata", "textFragment", this, currentField.getDisplay() + ":").add(new AttributeAppender("style", new Model("font-weight:bold;"), ";")));
                columnitem.add(new AttributeAppender("class", new Model("tag"), " ")).add(new AttributeAppender("style", new Model("color:red"), ";"));
                fieldNames.add(currentField.getName());
              }else{
                columnitem.add(new TextFragment("editdata", "textFragment", this, currentField.getDisplay() + ":").add(new AttributeAppender("style", new Model("font-weight:bold"), ";")));
                columnitem.add(new AttributeAppender("class", new Model("tag"), " "));
                fieldNames.add(currentField.getName());
              }
						} else {

							Object rawvalue = data.get(currentField.getName());
							rawvalue = (rawvalue == null) ? "" : rawvalue;
							 String value =
							 CRMUtility.formatValue(currentField.getFormatter(),
							 String.valueOf(rawvalue));
							 value = (value == null) ? "" : value;
							 
							 if(currentField.getName().equals("address")){
							   IModel<String> textModel = new Model<String>(value);
							   models.put(CRMUtility.formatValue(currentField.getFormatter(),String.valueOf(rawvalue)),textModel);
	               fieldNameToModel.put(currentField.getName(), textModel);
	                columnitem.add(new Textarea("editdata","textAreaFragment", this, textModel));
	              }else{
	                IModel<String> textModel = new Model<String>("");
	                models.put(CRMUtility.formatValue(currentField.getFormatter(),String.valueOf(rawvalue)),textModel);
	                fieldNameToModel.put(currentField.getName(), textModel);
	                columnitem.add(new TextInputFragment("editdata","textInputFragment", this, textModel,value,currentField));
	              }
						}
					}
					columnRepeater.add(columnitem);
				}
			}// end of set the detailed info
		}// end of groupNames loop
		Form form = new Form("form") {
			@Override
			protected void onSubmit() {
				logger.debug("the form was submitted!");
				logger.debug(models);
				System.out.println("models:"+models);
				List<String> values = Lists.newArrayList();
				List<String> names = Lists.newArrayList();
				boolean flag = true;
				for (String k : fieldNameToModel.keySet()) {
					names.add(k);
					Field field = schema.getFieldByName(k);
                    logger.debug("currentField:"+field);
                    //判断filed是否能为空，若为空则给出提示，不执行保存事件，若不为空在执行保存事件
                    if(field.isRequired()){
                    	if(null==(String) fieldNameToModel.get(k).getObject()){
                    		//如果为空写出提示信息
                    		logger.debug("fieldName:"+field.getName()+"不能为空");
                    		div.add(new AttributeAppender("style",new Model("display:block"),";"));
                    		divitem.add(new AttributeAppender("style",new Model("display:block"),";"));
                    		promptLabel.add(new AttributeAppender("style",new Model("display:block"),";"));
                    		promptButton.add(new AttributeAppender("style",new Model("display:block"),";"));
                    		flag = false;
                    		return;
                    	}
                	}
					Object obj = fieldNameToModel.get(k).getObject() ;
					String value = null;
					if(obj!=null){
					   if(obj instanceof Choice){
					       value = String.valueOf(((Choice)obj).getId());
					   }else{
					       value = "'"+String.valueOf(obj)+"'"; 
					   }
					}
					//String value = fieldNameToModel.get(k).getObject() == null? null:"'"+String.valueOf(fieldNameToModel.get(k).getObject())+"'";
					values.add(value);
				}
				DAOImpl.updateRecord(entityId,schema.getName(),names, values);
				setResponsePage(new EntityDetailPage(schema.getName(),entityId));
			}
		};
		form.add(fieldGroupRepeater);
		add(form);
		// set navigation bar active
		add(new AbstractAjaxBehavior() {
			@Override
			public void onRequest() {
			}
			@Override
			public void renderHead(Component component, IHeaderResponse response) {
				super.renderHead(component, response);
				response.render(OnDomReadyHeaderItem.forScript("$(\"#navitem-"
						+ schema.getName() + "\").addClass(\"active\");"));
			}
		});
	}

	public EditDataFormPanel(String id, IModel<?> model) {
		super(id, model);
	}

	private class TextFragment extends Fragment {

		public TextFragment(String id, String markupId,
				MarkupContainer markupProvider, String label) {
			super(id, markupId, markupProvider);
			add(new Label("celldata", label));
			// TODO Auto-generated constructor stub
		}
	}

	private class LayoutFragment extends Fragment {

		public LayoutFragment(String id, String markupId,
				MarkupContainer markupProvider, String label) {
			super(id, markupId, markupProvider);
			add(new Label("add", label).setEscapeModelStrings(false));
			// TODO Auto-generated constructor stub
		}

	}
    private class RelationTableSearchFragment extends Fragment {
        public RelationTableSearchFragment(String id, String markupId,
                MarkupContainer markupProvider, final String entityName,final String value, IModel model,final String eid) {
            super(id, markupId, markupProvider);

            PageParameters params = new PageParameters();
            params.set("en", entityName);
            params.set("target", (long)model.getObject());
            params.set("eid", eid);
            
            PopupSettings popupSettings = new PopupSettings("查找").setHeight(470)
                    .setWidth(850).setLeft(150).setTop(200);
            add(new BookmarkablePageLink<Void>("search_btn", SelectEntryPage.class,params).setPopupSettings(popupSettings));
            HiddenField<?>  hidden = new HiddenField<String>("selected_id_hidden" ,model);
            hidden.add(new AttributeAppender("id",entityName+"_id"));
            add(hidden);
            TextField<String> text = new TextField<String>("selected_value_input" ,new Model(value));
            text.add(new AttributeAppender("id",entityName+"_name"));
            add(text);
        }
    }
	private class DropDownChoiceFragment extends Fragment {
		public DropDownChoiceFragment(String id, String markupId,
				MarkupContainer markupProvider, final List<Long> ids,
				final Map<Long, String> list, IModel model) {
			super(id, markupId, markupProvider);
			add(new DropDownChoice<Long>("dropDownInput", model, ids,
					new IChoiceRenderer<Long>() {
						@Override
						public Object getDisplayValue(Long id) {
							// TODO Auto-generated method stub
							return list.get(id);
						}

						@Override
						public String getIdValue(Long id, int index) {
							return String.valueOf(id);
						}
					}));
		}
		
        public DropDownChoiceFragment(String id, String markupId,MarkupContainer markupProvider,
                IModel choices,IModel default_model,final Entity entity, final Field currentField){
            super(id, markupId, markupProvider);
            DropDownChoice dropDown = createDropDownListFromPickList("dropDownInput",choices,default_model);
            add(dropDown);
            
            if(currentField.getChildNode()!=null){
                parentModels.put(currentField.getName(), default_model);
                //childDropDownComponent.put(field.get("child-pl"), null);
                
                dropDown.add(new AjaxFormComponentUpdatingBehavior("onchange"){

                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {
                        //System.out.println("TTTT:"+field.get("name"));
                        //System.out.println("childDropDownComponent:"+childDropDownComponent);
                        target.add(childDropDownComponent.get( currentField.getChildNode()));
                        //System.out.println("currentfield code:"+String.valueOf(((Choice)parentModels.get(field.get("name")).getObject()).getCode()));
                            //System.out.println("DDDD:"+table.get(field.get("childNode")).get("childNode"));
                            if( entity.getFieldByName(currentField.getChildNode()).getChildNode() != null ){
                              
                               parentModels.get(entity.getFieldByName(currentField.getChildNode()).getName()).setObject(new Choice(-1L,""));
                               target.add(childDropDownComponent.get(entity.getFieldByName(currentField.getChildNode()).getChildNode()));
                               // target.
                            }
                        
                    }
                    
                    
                   
                    
                 });
             }
            
        } 
	}
    private DropDownChoice createDropDownListFromPickList(String markupId,IModel choices,IModel default_model) {
        return new DropDownChoice<Choice>(markupId, default_model, choices,
                new IChoiceRenderer<Choice>() {
                    @Override
                    public Object getDisplayValue(Choice choice) {
                        // TODO Auto-generated method stub
                        return choice.getVal();
                    }
                    @Override
                    public String getIdValue(Choice choice, int index) {
                        // TODO Auto-generated method stub
                        return String.valueOf(choice.getId());
                    }
                    
                    
                });
    }

private class Textarea extends Fragment {
    
    public Textarea(String id, String markupId, MarkupContainer markupProvider,IModel<String> value)
    {
      super(id, markupId, markupProvider);
      // TODO Auto-generated constructor stub
      add(new TextArea<String> ("address",value));
      
    }

}
	private class TextInputFragment extends Fragment {
		public TextInputFragment(String id, String markupId,
				MarkupContainer markupProvider, IModel model,String value,Field currentField) {
			super(id, markupId, markupProvider);
			TextField<String> text = new TextField<String>("input", model);
							if(!currentField.isEditable()){
								if(currentField.getName().equals("accountId")){
									text.add(new AttributeModifier("disabled",new Model("disabled")));
								}else if(currentField.getName().equals("modifier")){
									text.add(new AttributeModifier("value", new Model(user)));
									text.add(new AttributeModifier("readonly",new Model("readonly")));
								}else if(currentField.getName().equals("modify_datetime")){
										Calendar calendar = Calendar.getInstance();  
										final SimpleDateFormat dateformat = new SimpleDateFormat("YYYY-MM-dd HH:mm");
										Date time =(Date)calendar.getTime();
										String modify_datetime = dateformat.format(time);
										IModel modifiedTimeModel = new Model(){
											@Override
											public Serializable getObject() {
												Date time = new Date(System.currentTimeMillis());
												return  dateformat.format(time);
											}
				                        };
										text.add(new AttributeModifier("value",modifiedTimeModel));
										text.add(new AttributeModifier("readonly",new Model("readonly")));
								}else{
									text.add(new AttributeModifier("value", new Model(value)));
									text.add(new AttributeModifier("readonly",new Model("readonly")));
								}
							}else{
								text.add(new AttributeModifier("value", new Model(value)));
							}
							add(text);
							text.add(new AttributeAppender("type",new Model(currentField.getDataType()),";"));
							if(currentField.getDataType().equals("tel")||currentField.getName().equals("fax")||currentField.getName().equals("office_fax")){
								text.add(new AttributeModifier("pattern",new Model("^((\\d{11})|^((\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1})|(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1}))$)")));
							}
							if(currentField.isRequired()){
								text.add(new AttributeAppender("required",new Model("required"),";"));
							}
		}
	}

}
