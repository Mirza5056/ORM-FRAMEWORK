import com.kamran.akthar.pojo.annotation.*;
import com.kamran.akthar.pojo.datamanager.*;
@Table(name="student")
public class Student 
{
@PrimaryKey
@Column(name="roll_number")
public int rollNumber;
public void setRollNumber(int rollNumber)
{
this.rollNumber=rollNumber;
}
@Column(name="first_name")
public String firstName;
public void setFirstName(String firstName)
{
this.firstName=firstName;
}
@Column(name="last_name")
public String lastName;
public void setLastName(String lastName)
{
this.lastName=lastName;
}
@Column(name="aadhar_card_number")
public int aadharCardNumber;
public void setAadharCardNumber(int aadharCardNumber)
{
this.aadharCardNumber=aadharCardNumber;
}
@Column(name="course_code")
@ForeginKey(parent="course",column="code")
public int courseCode;
public void setCourseCode(int courseCode)
{
this.courseCode=courseCode;
}
@Column(name="gender")
public String gender;
public void setGender(String gender)
{
this.gender=gender;
}
@Column(name="dat_of_birth")
public date datOfBirth;
public void setDatOfBirth(date datOfBirth)
{
this.datOfBirth=datOfBirth;
}
}
class 
{
public static void main(String gg[])
{
}
}