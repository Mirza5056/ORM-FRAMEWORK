package com.kamran.akthar.pojo;
import com.kamran.akthar.pojo.datamanager.*;
import java.sql.*;
import java.io.*;
import java.util.*;
import java.lang.annotation.*;
import java.lang.reflect.*;
import com.google.gson.*;
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface Table 
{
    String name() default "";
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface Column 
{
    String name() default "";
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface PrimaryKey {}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface AutoIncrement {}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface ForeginKey {
    String parent() default "";
    String column() default "";
}

class start
{
    public static void main(String gg[])
    {
        try
        {
            /* Give database connectivity file path according to you..... */
            FileReader reader=new FileReader("C:/ORM/framework/testcase/conf.json");
            JsonObject json=JsonParser.parseReader(reader).getAsJsonObject();
            String driver=json.get("jdbc_Driver").getAsString();
            String connection=json.get("connection_url").getAsString();
            String username=json.get("user_name").getAsString();
            String password=json.get("password").getAsString();
            Class.forName(driver);
            Connection conn=DriverManager.getConnection(connection,username,password);
            DatabaseMetaData metaData=conn.getMetaData();
            ResultSet resultSet=metaData.getTables(null,null,null,new String[]{"TABLE"});
            while(resultSet.next())
            {
                String tableName=resultSet.getString("TABLE_NAME");
                ResultSet columns=metaData.getColumns(null,null,tableName,null);
                StringBuilder fileContent=new StringBuilder();
                fileContent.append("import com.kamran.akthar.pojo.annotation.*;\n");
                fileContent.append("import com.kamran.akthar.pojo.datamanager.*;\n");
                fileContent.append("@Table(name=\""+tableName+"\")\n");
                fileContent.append("public class ").append(tableName.substring(0,1).toUpperCase()+tableName.substring(1).toLowerCase()).append(" \n{\n");
                while (columns.next()) 
                {
                    String columnName = columns.getString("COLUMN_NAME");
                    String columnType = columns.getString("TYPE_NAME");
                    String columnDefault = columns.getString("COLUMN_DEF");
                    String FkColumnName = "";
                    String PKTableName = "";
                    String PKColumnName = "";
                    ResultSet importKeys = metaData.getImportedKeys(null, null, tableName);
                    ResultSet primaryKey=metaData.getPrimaryKeys(null,null,tableName);
                    List<String> importedKeysColumn = new ArrayList<>();
                    List<String> primaryKeyColumn=new ArrayList<>();
                    while (importKeys.next()) 
                    {
                        FkColumnName = importKeys.getString("FKCOLUMN_NAME");
                        PKTableName = importKeys.getString("PKTABLE_NAME");
                        PKColumnName = importKeys.getString("PKCOLUMN_NAME");
                        importedKeysColumn.add(FkColumnName);
                    }
                    while(primaryKey.next())
                    {
                        primaryKeyColumn.add(primaryKey.getString("COLUMN_NAME"));
                    }
                    if (primaryKeyColumn.contains(columnName)) 
                    {
                        fileContent.append("@PrimaryKey\n");
                    }
                    fileContent.append("@Column(name=\""+columnName+"\")\n");
                    if (importedKeysColumn.contains(columnName)) 
                    {
                        fileContent.append("@ForeginKey(parent=\"" + PKTableName + "\",column=\"" + PKColumnName + "\")\n");
                    }
                    fileContent.append("public "+columnType.toLowerCase().replace("char","String")+" "+convertSnakeToCamel(columnName)+";\n");
                    fileContent.append("public void set"+convertSnakeToCamel2(columnName)+"("+columnType.toLowerCase().replace("char","String")+" "+convertSnakeToCamel(columnName)+")\n");
                    fileContent.append("{\n");
                    fileContent.append("this."+convertSnakeToCamel(columnName)+"="+convertSnakeToCamel(columnName)+";\n");
                    fileContent.append("}\n");
                }
                fileContent.append("}\n");
                fileContent.append("class \n").append("{\n");
                fileContent.append("public static void main(String gg[])\n").append("{\n");
                fileContent.append("}\n");
                fileContent.append("}\n");
                try 
                {
                    FileWriter writer = new FileWriter(tableName.substring(0,1).toUpperCase()+tableName.substring(1).toLowerCase()+ ".java");
                    writer.write(fileContent.toString());
                    writer.close();
                } catch (IOException io) 
                {
                    System.out.println(io);
                }
                System.out.println("Table-Name :"+tableName);
            }
            conn.close();
        }catch(Exception e)
        {
            System.out.println(e);
        }
    }
    public static String convertSnakeToCamel(String snakeCase) {
        StringBuilder camelCase = new StringBuilder();
        boolean capitalizeNext = false;

        for (char c : snakeCase.toCharArray()) {
            if (c == '_') {
                capitalizeNext = true;
            } else {
                if (capitalizeNext) {
                    camelCase.append(Character.toUpperCase(c));
                    capitalizeNext = false;
                } else {
                    camelCase.append(Character.toLowerCase(c));
                }
            }
        }

        return camelCase.toString();
    }
    public static String convertSnakeToCamel2(String snakeCase) {
        StringBuilder camelCase = new StringBuilder();
        boolean capitalizeNext = true;

        for (char c : snakeCase.toCharArray()) {
            if (c == '_') {
                capitalizeNext = true;
            } else {
                if (capitalizeNext) {
                    camelCase.append(Character.toUpperCase(c));
                    capitalizeNext = false;
                } else {
                    camelCase.append(Character.toLowerCase(c));
                }
            }
        }

        return camelCase.toString();
    }
}