package com.kamran.akthar.pojo.datamanager;
/* Made By Kamran Akthar */
import com.kamran.akthar.pojo.annotation.*;
import java.sql.*;
import java.lang.annotation.*;
import java.io.*;
import com.google.gson.*;
import java.lang.annotation.*;
import java.lang.reflect.*;
import java.text.*;
public class DataManager
{
    public static DataManager instance;
    private Connection conn;
    private boolean isBeginCalled=false;
    private boolean isEndCalled=false;
    public DataManager()
    {
    try
    {
        FileReader reader=new FileReader("C:/ORM/framework/testcase/conf.json");
        JsonObject json=JsonParser.parseReader(reader).getAsJsonObject();
        String driver=json.get("jdbc_Driver").getAsString();
        String connection=json.get("connection_url").getAsString();
        String username=json.get("user_name").getAsString();
        String password=json.get("password").getAsString();
        Class.forName(driver);
        conn=DriverManager.getConnection(connection,username,password);
    }catch(Exception e)
    {
        System.out.println(e);
    }
    }
    public static DataManager getDataManager()
    {
        if(instance==null)
        {
            instance=new DataManager();
        }
        return instance;
    }
    public void begin() 
    {
        isBeginCalled=true;
    }
    public void end()
    {
        isEndCalled=true;
    }
    public void save(Object obj)
    {
        if(!isBeginCalled)
        {
            throw new IllegalStateException("begin() must be called before save()");
        }
        String tableName=obj.getClass().getAnnotation(Table.class).name();
        Field[] fields=obj.getClass().getDeclaredFields();
        StringBuilder sql=new StringBuilder("INSERT INTO "+tableName+" (");
        StringBuilder values=new StringBuilder("VALUES (");
        for(Field field : fields)
        {
            Column columnAnnotation=field.getAnnotation(Column.class);
            if(columnAnnotation!=null)
            {
                String columnName=columnAnnotation.name();
                field.setAccessible(true);
                try
                {
                    Object value=field.get(obj);
                    if(value instanceof String)
                    {
                        values.append("'").append(value).append("'");
                    }
                    else if(value instanceof java.sql.Date)
                    {
                        values.append("'").append(value).append("'");
                    }
                    else if (value instanceof Character)
                    {
                        values.append("'").append(value).append("'");
                    }
                    else
                    {
                        values.append(value);
                    }
                    sql.append(columnName).append(",");
                    values.append(",");
                }catch(IllegalAccessException iae)
                {
                    System.out.println(iae);
                }
            }
        }
        sql.deleteCharAt(sql.length()-1);
        values.deleteCharAt(values.length()-1);
        sql.append(")").append(values).append(")");
        try
        {
            Statement stm=conn.createStatement();
            stm.executeUpdate(sql.toString());
        }catch(SQLException e)
        {
            System.out.println(e);
        }
        System.out.println(sql);
    }


    public void update(Object obj) 
    {
        if(!isBeginCalled)
        {
            throw new IllegalStateException("begin() must be called before update()");
        }
        String tableName = obj.getClass().getAnnotation(Table.class).name();
        Field[] fields = obj.getClass().getDeclaredFields();
        StringBuilder sql = new StringBuilder("UPDATE " + tableName + " SET ");    
        // Find the primary key field
        Field primaryKeyField = null;
        for (Field field : fields) 
        {
            PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
            if (primaryKey != null) 
            {
                primaryKeyField = field;
                break;
            }
        }
        if (primaryKeyField != null) 
        {
            try 
            {
                primaryKeyField.setAccessible(true);
                Object primaryKeyValue = primaryKeyField.get(obj);
                if (primaryKeyValue != null) 
                {
                    for (Field field : fields) 
                    {
                        Column columnAnnotation = field.getAnnotation(Column.class);
                        if (columnAnnotation != null && field != primaryKeyField) 
                        {
                            String columnName = columnAnnotation.name();
                            field.setAccessible(true);
                            Object value = field.get(obj);
                            if (value instanceof String) 
                            {
                                sql.append(columnName).append("='").append(value).append("', ");
                            } 
                            else if(value instanceof java.sql.Date) 
                            {
                                System.out.println("Hello");
                                sql.append(columnName).append("='").append(value).append("', ");
                            } 
                            else if(value instanceof Character) 
                            {
                                sql.append(columnName).append("='").append(value).append("', ");
                            } 
                            else 
                            {
                                sql.append(columnName).append("=").append(value).append(", ");
                            }
                        }
                    }                    
                    sql.delete(sql.length() - 2, sql.length()); // Remove the trailing comma and space
                    sql.append(" WHERE ");
                    sql.append(primaryKeyField.getAnnotation(Column.class).name()).append("=").append(primaryKeyValue);
                    try 
                    {
                        Statement stm = conn.createStatement();
                        stm.executeUpdate(sql.toString());
                    } catch (SQLException e) {
                        System.out.println(e);
                    }
                }
                System.out.println(sql);
            }catch(IllegalAccessException iae) 
            {
                System.out.println(iae);
            }
        }
    }

    public void delete(Object obj)
    {
        if(!isBeginCalled)
        {
            throw new IllegalStateException("begin() must be called before delete()");
        }
        String tableName=obj.getClass().getAnnotation(Table.class).name();
        Field[] fields=obj.getClass().getDeclaredFields();
        StringBuilder sql=new StringBuilder("DELETE FROM "+tableName+" WHERE ");
        String primaryKeyColumnName=null;
        String columnNameClass=null;
        for(Field field:fields)
        {
            PrimaryKey primaryKey=field.getAnnotation(PrimaryKey.class);
            //Column columnAnnotation=field.getAnnotation(Column.class);
            if(primaryKey!=null)
            {
                String columnName=field.getAnnotation(Column.class).name();
                field.setAccessible(true);
                try
                {
                    Object value=field.get(obj);
                    if(value instanceof String)
                    {
                        sql.append(columnName).append("=").append(value).append("");
                    }
                    else
                    {
                        sql.append(columnName).append("=").append(value);
                    }
                }catch(IllegalAccessException iae)
                {
                    System.out.println(iae.getMessage());
                }
            }
        }        
        try
        {
            Statement stm=conn.createStatement();
            stm.executeUpdate(sql.toString());
        }catch(SQLException sl)
        {
            System.out.println(sl.getMessage());
        }
        System.out.println(sql);
    }
}