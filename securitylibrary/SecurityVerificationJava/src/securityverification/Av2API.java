package securityverification;

import static org.osate.xtext.aadl2.properties.util.GetProperties.*;
import static org.osate.xtext.aadl2.properties.util.PropertyUtils.*;

import java.util.Collection;
import java.util.function.Predicate;

import org.eclipse.emf.ecore.EObject;
import org.osate.aadl2.BooleanLiteral;
import org.osate.aadl2.ComponentCategory;
import org.osate.aadl2.EnumerationLiteral;
import org.osate.aadl2.IntegerLiteral;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.NamedValue;
import org.osate.aadl2.PortConnection;
import org.osate.aadl2.Property;
import org.osate.aadl2.PropertyExpression;
import org.osate.aadl2.RealLiteral;
import org.osate.aadl2.RecordValue;
import org.osate.aadl2.StringLiteral;
import org.osate.aadl2.instance.ComponentInstance;
import org.osate.aadl2.instance.InstanceObject;
import org.osate.aadl2.properties.PropertyLookupException;
import org.osate.result.Diagnostic;
import org.osate.result.Result;
import org.osate.result.util.ResultUtil;

public class Av2API {
	
	// methods for retrieving property values of a certain type
	
	// Performance: lookup every time is nine times slower than cached property definition 100,000 loopups 100ms vs. 970ms
	
	public static boolean getBooleanProperty(InstanceObject io,String propname,  boolean defaultvalue) {
		Property pd = lookupPropertyDefinition(io, propname);
		return getBooleanValue(io, pd,defaultvalue);
	}
	
	public static long getIntegerProperty(InstanceObject io,String propname, long defaultvalue) {
		Property pd = lookupPropertyDefinition(io, propname);
		return getIntegerValue(io, pd,defaultvalue);
	}
	
	public static double getRealProperty(InstanceObject io,String propname, double defaultvalue) {
		Property pd = lookupPropertyDefinition(io, propname);
		return getRealValue(io, pd,defaultvalue);
	}
	
	public static String getStringProperty(InstanceObject io,String propname, String defaultvalue) {
		Property pd = lookupPropertyDefinition(io, propname);
		return getStringValue(io, pd,defaultvalue);
	}
	
	public static EnumerationLiteral getLiteralProperty(InstanceObject io, String propname) {
		Property pd = lookupPropertyDefinition(io, propname);
		try{
			return getEnumLiteral(io, pd);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String getLiteralPropertyAsString(InstanceObject io, String propname, String defaultvalue) {
		Property pd = lookupPropertyDefinition(io, propname);
		try{
			return getEnumLiteral(io, pd).getName();
		} catch (Exception e) {
			return defaultvalue;
		}
	}
	
	public static RecordValue getRecordProperty(String propname, InstanceObject io) {
		Property pd = lookupPropertyDefinition(io, propname);
		try {
			return (RecordValue) getSimplePropertyValue(io, pd);
		} catch (PropertyLookupException e) {
			return null;
		}
	}
	
	public static boolean getBooleanRecordField(RecordValue rv, String fieldname) {
		PropertyExpression pe = getRecordFieldValue(rv, fieldname);
		if (pe instanceof BooleanLiteral) {
			return ((BooleanLiteral)pe).getValue();
		}
		return false;
	}

	public static long getIntegerRecordField(RecordValue rv, String fieldname) {
		PropertyExpression pe = getRecordFieldValue(rv, fieldname);
		if (pe instanceof IntegerLiteral) {
			return ((IntegerLiteral)pe).getValue();
		}
		return 0;
	}

	public static double getRealRecordField(RecordValue rv, String fieldname) {
		PropertyExpression pe = getRecordFieldValue(rv, fieldname);
		if (pe instanceof RealLiteral) {
			return ((RealLiteral)pe).getValue();
		}
		return 0.0;
	}

	public static String getStringRecordField(RecordValue rv, String fieldname) {
		PropertyExpression pe = getRecordFieldValue(rv, fieldname);
		if (pe instanceof StringLiteral) {
			return ((StringLiteral)pe).getValue();
		}
		return "";
	}

	public static String getLiteralAsStringRecordField(RecordValue rv, String fieldname) {
		PropertyExpression pe = getRecordFieldValue(rv, fieldname);
		if (pe instanceof NamedValue) {
			return ((EnumerationLiteral) ((NamedValue) pe).getNamedValue()).getName();
		}
		return "";
	}

	public static EnumerationLiteral getLiteralRecordField(RecordValue rv, String fieldname) {
		PropertyExpression pe = getRecordFieldValue(rv, fieldname);
		if (pe instanceof NamedValue) {
			return (EnumerationLiteral) ((NamedValue) pe).getNamedValue();
		}
		return null;
	}

	
	// in GetProperties
	// methods to find property definition, type, constant, or enumeration/unit literal from the type
	// the results are often used in retrieving property values (see above).
	// 	public static boolean hasAssignedPropertyValue(NamedElement element, String qpname) 
	// 	public static Property lookupPropertyDefinition(EObject context, String qpname)
	// 	public static PropertyConstant lookupPropertyConstant(EObject context, String qname)
	// 	public static PropertyType lookupPropertyType(EObject context, String qname)
	// 	public static UnitLiteral findUnitLiteral(Element context, String unitsType, String literal)
	// 	public static EnumerationLiteral findEnumerationLiteral(Element context, String enumerationType, String literal) 
	//
	// GetProperties has lots os value retrieval methods for predeclared properties.
	
	// determines whether an element exists in the collection that satisfies the lambda expression
	
	public static <T>
	boolean exists(Collection<? extends T> c, Predicate<? super T> p) {

	    for (final T z : c) {
	        if (p.test(z)) {
	            return true;
	        }
	    }
	    return false;
	}

	public static <T>
	boolean forAll(Collection<? extends T> c, Predicate<? super T> p) {

	    for (final T z : c) {
	        if (p.negate().test(z)) {
	            return false;
	        }
	    }
	    return true;
	}
	
	public static boolean implies (boolean first, boolean second) {
		return !first?true:second;
	}
	
	// methods to retrieve collections of objects from instance model
	public static Collection<ComponentInstance> getAllData(ComponentInstance context){
		return context.getAllComponentInstances(ComponentCategory.DATA);
	}
	
	// convenience methods for recording Diagnostic issues
	public static void addFailure(Result result, String failuremsg, NamedElement target) {
		Diagnostic issue = ResultUtil.createFailure(failuremsg,target);
		result.getDiagnostics().add(issue);
	}
	
	
	// when conditions for requirements
	
	public static boolean isPortConnection(EObject conn) {
		return conn instanceof PortConnection;
	}

}
