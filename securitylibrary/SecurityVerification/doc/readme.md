Writing verification methods and requirements when conditions in Java

Requirement when conditions are assumed to be Java methods that accept a single parameter of type EObject.
The parameter actual will be a model element of a declarative AADL model.

Verification methods written in Java have as first parameter an instance model element that is the target of the requirement 
(component instance as specified in the requirement set or a target element within the component as specified by the 'for' in a specific requirement).
Supported return values are boolean, Result, and other values to be assigned to compute variables (see ALISA documentation for full details).



Useful utility methods for writing verification methods

Org.osate.aadl plugin
Methods directly associated with elements of the declarative and instance Meta model

Aadl2Util: (org.osate.aadl)
isNull, isUnresolved, sameProperty, sameUnit

Aadl2InstanceUtil: (org.osate.aadl)
Connection instance related methods – outgoing connections, etc.

InstanceModelUtil: (org.osate.xtext.aadl2.properties)
Binding related utility methods
Connectivity related – connectedbybus
isPeriodicThread etc. methods

PropertyUtil: (org.osate.xtext.aadl2.properties)
generic get value methods

GetProperties: (org.osate.xtext.aadl2.properties)
specific core methods, 
isAssigned methods
scale values to specific unit

FlowLatencyUtil: (org.osate.analysis.flows)
Access methods for ARINC653 properties
Connection – data, type
NamedElement dimension
Methods for processing ETEF instances
