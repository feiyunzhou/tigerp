package com.rex.crm.common;

import java.io.Serializable;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

public class Entity implements Serializable
{
  @Expose
  private String name;
  @Expose
  private String display;
  @Expose
  private List<Field> fields;
  @Expose
  private String sql;
  @Expose
  private String filterField;
  @Expose
  private String sql_ent;
  @Expose
  private String sqlAdmin;  //for the admin to query all data
  @Expose
  private String sqlManager;
  @Expose
  private String sqlCoaching;
  @Expose
  private String sqlAdminCoaching;  //for the admin to query all data
  @Expose
  private String sqlManagerCoaching;
  @Expose
  private String sqlManagerCalendar;
  @Expose
  private String sqlCalendar;
  @Expose
  private String sqlAdminCalendar;
  @Expose
  private String externalField;
  @Expose
  private boolean globalsearch;
  
  public boolean isGlobalsearch() {
	return globalsearch;
}

public void setGlobalsearch(boolean globalsearch) {
	this.globalsearch = globalsearch;
}

public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getDisplay()
  {
    return display;
  }

  public void setDisplay(String display)
  {
    this.display = display;
  }

  public List<Field> getFields()
  {
    return fields;
  }

  public void setFields(List<Field> fields)
  {
    this.fields = fields;
  }

  @Override
  public String toString()
  {
    GsonBuilder gb = new GsonBuilder();
    StringBuffer sb = new StringBuffer();
    sb.append(gb.excludeFieldsWithoutExposeAnnotation().create().toJson(this));
    return sb.toString();
  }

  public List<String> getFieldNames()
  {
    List<String> fd = Lists.newArrayList();
    if (fields != null)
    {
      for (Field f : fields)
      {
        fd.add(f.getName());
      }
    }
    return fd;
  }

  public String getPrimaryKeyName()
  {
    if (fields != null)
    {
      for (Field f : fields)
      {
        if (f.isPrimaryKey())
        {
          return f.getName();
        }
      }
    }
    return null;
  }

  public List<Field> getSearchableFields()
  {
    List<Field> res = Lists.newArrayList();
    if (fields != null)
    {
      for (Field f : fields)
      {
        if (f.isSearchable())
        {
          res.add(f);
        }
      }
    }
    return res;
  }

  public List<Field> getParamFields()
  {
    List<Field> res = Lists.newArrayList();
    if (fields != null)
    {
      for (Field f : fields)
      {
        if (f.isParam())
        {
          res.add(f);
        }
      }
    }
    return res;
  }

  public List<Field> getAutoFields()
  {
    List<Field> res = Lists.newArrayList();
    if (fields != null)
    {
      for (Field f : fields)
      {
        if (f.getFieldType() != null && f.getFieldType().equalsIgnoreCase("auto"))
        {
          res.add(f);
        }
      }
    }
    return res;
  }
  
  public List<Field> getImportFields()
  {
    List<Field> res = Lists.newArrayList();
    if (fields != null)
    {
      for (Field f : fields)
      {
        if (f.getImport_field_name() != null)
        {
          res.add(f);
        }
      }
    }
    return res;
  }
  
  public List<Field> getForeignKeyFields()
  {
    List<Field> res = Lists.newArrayList();
    if (fields != null)
    {
      for (Field f : fields)
      {
        if (f.getRelationTable()!= null)
        {
          res.add(f);
        }
      }
    }
    return res;
  }
  
  public List<Field> getImportForeignKeyFields()
  {
    List<Field> res = Lists.newArrayList();
    if (fields != null)
    {
      for (Field f : fields)
      {
        if (f.getRelationTable()!= null && f.getImport_field_name()!=null)
        {
          res.add(f);
        }
      }
    }
    return res;
  }

  public List<String> getDisplayNames()
  {
    List<String> fd = Lists.newArrayList();
    if (fields != null)
    {
      for (Field f : fields)
      {
        fd.add(f.getDisplay());
      }
    }
    return fd;
  }

  public String getSql()
  {
    return sql;
  }

  public void setSql(String sql)
  {
    this.sql = sql;
  }

  public String getFilterField()
  {
    return filterField;
  }

  public void setFilterField(String filterField)
  {
    this.filterField = filterField;
  }

  public Field getFieldByName(String name)
  {
    if (fields != null)
    {
      for (Field f : fields)
      {
        if (f.getName().equalsIgnoreCase(name))
        {
          return f;
        }
      }
    }

    return null;
  }

  public String getSql_ent()
  {
    return sql_ent;
  }

  public void setSql_ent(String sql_ent)
  {
    this.sql_ent = sql_ent;
  }

  public String getSqlAdmin()
  {
    return sqlAdmin;
  }

  public void setSqlAdmin(String sqlAdmin)
  {
    this.sqlAdmin = sqlAdmin;
  }

  public String getSqlManager()
  {
    return sqlManager;
  }

  public void setSqlManager(String sqlManager)
  {
    this.sqlManager = sqlManager;
  }

  public void setSqlManagerCalendar(String sqlManagerCalendar)
  {
    this.sqlManagerCalendar = sqlManagerCalendar;
  }

  public String getSqlManagerCalendar()
  {
    return sqlManagerCalendar;
  }

  public void setSqlAdminCalendar(String sqlAdminCalendar)
  {
    this.sqlAdminCalendar = sqlAdminCalendar;
  }

  public String getSqlAdminCalendar()
  {
    return sqlAdminCalendar;
  }

  public void setSqlCalendar(String sqlCalendar)
  {
    this.sqlCalendar = sqlCalendar;
  }

  public String getSqlCalendar()
  {
    return sqlCalendar;
  }

  public String getSqlCoaching()
  {
    return sqlCoaching;
  }

  public void setSqlCoaching(String sqlCoaching)
  {
    this.sqlCoaching = sqlCoaching;
  }

  public String getSqlAdminCoaching()
  {
    return sqlAdminCoaching;
  }

  public void setSqlAdminCoaching(String sqlAdminCoaching)
  {
    this.sqlAdminCoaching = sqlAdminCoaching;
  }

  public String getSqlManagerCoaching()
  {
    return sqlManagerCoaching;
  }

  public void setSqlManagerCoaching(String sqlManagerCoaching)
  {
    this.sqlManagerCoaching = sqlManagerCoaching;
  }

public String getExternalField() {
    return externalField;
}

public void setExternalField(String externalField) {
    this.externalField = externalField;
}
}
