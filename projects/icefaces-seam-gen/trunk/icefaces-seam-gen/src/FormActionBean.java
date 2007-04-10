package @actionPackage@;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.log.Log;
import org.jboss.seam.core.FacesMessages;
import org.hibernate.validator.Length;

@Stateful 
@Name("@componentName@")
public class @beanName@ implements @interfaceName@ {

    @Logger private Log log;
    
    @In FacesMessages facesMessages;
    
    private String value;
	
    public void @methodName@()
	 {
        //implement your business logic here
        log.info("@componentName@.@methodName@() action called with: #{@componentName@.value}");
        facesMessages.add("@methodName@ #{@componentName@.value}");
	 }
	
	 //add additional action methods
	
    @Length(max=10)
    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    @Destroy @Remove                                                                      
    public void destroy() {}
    
}
