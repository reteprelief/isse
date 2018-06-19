# ISSE Security Verification


[TOC levels=2-4 bullet hierarchy]


This note provides a summary description of the *SecurityVerification* project. It provides 

- a set of security and security enforcement related properties
- a number of Resolute libraries with general and security specific utility functions,
- several Resolute libraries of verification related claim functions, 
- a Resolute library with vulnerability impact functions,
- a Resolute library containing a set of user-defined architecture design related constants, 
- ALISA verification method registries for the verification claim functions,
- a set of reusable global requirement specifications expressed in ReqSpec, and
- a set of verification plans to accompany the specified security requirements.

The project *SecurityAlisaExamples* contains several example system models to which the security requirements and verification plans are applied to.

First, we will describe the set of security related properties, then the security requirement specifications and their verification activities, and finally we will describe the Resolute implemented utility functions and claim verification functions.

We use the following naming conventions: 

- For properties and property constants we use the first letter upper case convention for each of the words in an identifier and we do not use '\_' to separate the words. We do the same for requirement, verification plan identifiers as well as for verification methods.

- In Resolute we use all lower case words separated by '\_' as identifiers. The packages containing Resolute libraries use both first letter upper case (AADL convention) and "\_" (Resolute convention).

> I integrated Dave's properties and Resolute functions with mine and cleaned them up. I also tired to systematically document all Resolute function either in their descriptions or as comments.

> I updated the Resolute documentation, fixed a couple of issues, updated the built-in function *debug* to write to a Resolute console, and added the ability to retrieve property constant values. These improvements are available in the nightly build.

## Security Related Properties

Security related properties are organized into two property sets: *SecurityClassificationProperties*, and *SecurityEnforcementProeprties*. In addition we have a property set with user defined property constants that associate labels to string value used to express security clearance and information security levels.

> Note that we could use enumeration types to represent the labels instead. 

### Security Classification Properties

Security classification properties focus on specifying classifying application components as well as hardware components. 
The security clearance properties include a primary 
security classification (e.g. Top Secret, Secret, Confidential)

We have *Information Security Level* classification focused on characterizing application components.

- InformationSecurityLevel: string - a primary 
 security classification (e.g. Top Secret, Secret, Confidential)

- InformationSecurityCaveats: string - and a caveat (e.g. control markings).Information security caveats are treated as a single string. For example, a US information caveat value may be "//SI/TK//RELIDO" such that the combined level and caveats classification marking may be "TOP SECRET//SI/TK//RELIDO" (currently not interpreted by the Resolute functions)

- NeedToKnowDomains: list of string	- application domains that the recipient is allowed to deal with. This can be certain types of information, e.g., personnel data, or certain types of system control, e.g., flight control vs. mission vs. entertainment.

> These properties can be associated with components as well as features. This allows users to characterize the information security level and need to know domains that are incoming and outgoing in the data/commands being received and sent. When associated with a component it represents the information security levels and need to know domains it is authorized to handle.

We have *Security Clearance*:

- SecurityClearance: string - a primary 
security classification (e.g. Top Secret, Secret, Confidential) to characterize hardware platform components in terms of clearance levels. Clearance levels are user definable and are expected to have an ordering, e.g., confidential, secret, topsecret. 

- SecurityClearanceSupplement: string - an optional supplemental security statement (currently not interpreted by the Resolute functions)

- SecondarySecurityClearance: string - The secondary security clearance is provided in the event of multiple clearances, e.g. a clearance from two different agencies. (currently not interpreted by the Resolute functions)

- SecondarySecurityClearanceSupplement: string - an optional supplemental security statement (currently not interpreted by the Resolute functions)

> No assumption is made about the relationship between the Security_Clearance property and the Secondary_Security_Clearance property.
  
- SecurityDomain: string - A security domain is the determining factor in the classification of an enclave of servers/computers. A network with a different security domain is kept separate from other networks. Examples: NIPRNet, SIPRNet. JWICS, NSANet are all kept separate. Systems in a domain are tagged with the same SecurityDomain value 

> Security domain seems to play a similar role to secondary security clearance. I used the description of security domain from wikipedia. Security domain is defined by the International Telecommunications Union as "a set of elements, a security policy, a security authority, and a set of security-relevant activities in which the elements are managed in accordance with the security policy" (Section 6.60; ITU-T X.1252). 

We have a property that deals with trust, i.e., the ability to handle multiple security clearances and need to know domains.

- Trusted: boolean - by default component are considered to not be trusted, i.e., they are not trusted if the user does not associate this property with a component or if they set the value to false.

There is also a property to characterize vulnerabilities.

### Security Enforcement Properties 

We have the physical exposure properties:

- Exposed: boolean - physical exposure or some other form of external exposure to security attacks

We have the encryption and authentication properties:

- Encryption: boolean - an indication that some form of encryption is used.

- EncryptionScheme: EncryptionType - a record of a particular kind of encryption used. (currently not interpreted by the Resolute functions)

- Authenticator: boolean - component is able to authenticate

- AuthenticationTypeAccess, AuthenticationProtocol, BlockCypherMode: security mechanisms. (currently not interpreted by the Resolute functions)

We have the space and time partition enforcement related properties:

- OS: string - the name of the OS a component is expected to run on. Associated with an application component. OSes are characterized by how they enforce space and time partitioning (see below).

- Language: string - name of the programming language used to generate code. Associated with an application component. Languages are characterized by how well they guarantee memory safe code execution.

Constants that define the characteristics of *OS* and *Langauge* are currently defined as Resolute global constants. We can define them as AADL property constants instead. They are:

- MemorySafeLanguages: set of string - languages that generate code that is memory safe, i.e., stays within its space, e.g., by limiting pointers

- VirtualMemoryOSes: set of string - Operating systems that enforce space partitioning through a virtual memory management mechanism.

NoVeritualMemoryOSes: set of string - Operatins systems that do not utilize virtual memory management

PartitionEnforcedTimingOSes: set of string - OSes that enforce compute time budgets at the partition level

ThreadEnforcedTinmingOSes: set of string - OSes that enforce compute time budgets at the thread level

## Reusable Global Security Requirement Specifications

The global requirement set and global requirement syntax and description can be found in the ALISA help section of OSATE under *Requirement Specification for a System/System Requirements*. 

General guidelines on how to use these constructs to define reusable and global requirements see *Guidelines for Requirements Modeling/Reusable and global Requirements* in the ALISA help section of OSATE. 

Here we describe several ways of how such requirements can be specified. The examples can be found in the file *SecurityReqs.reqspec* found in the *GlobalReqs* folder.

### Requirements for the System as a Whole

*BusSecurity* is defined as a generic requirement on bus related security. It gets refined into more specific requirements that are verifiable.

> In this case the claim for this requirement is satisfied if all its refined requirements are satisfied. Users could associate additional verification activities for the requirement being refined.

*SystemHasNoRelayChannel* is defined as a requirement that must be satisfied for the system as a whole, i.e., it is **for root**. In this case the verification activities are responsible for verifying all system components that are involved in meeting this system wide requirement. In the verification plan we call on a verification method that is also defined as operating on the **root** (see *SecurityClassificationResolute.methodregistry*).

> In this case the claim for the requirement and its verification activity results is associated with the instance model root only. The verification method is expected to provide details about the components being checked as part of its result. This Resolute Claim Result object is mapped into the assurance case instance structure. In the case of a Java implemented verification method the result is expected to be reported in an *org.osate.results* object.

### Requirements for Specific Component Categories, Features, Connections, Flows

*SharedBusSecurity* is defined as a requirement that applies to every instance of the bus category as indicated by **for bus**. 

> When ALISA generates the assurance case instance it will find all such instances and associate the appropriate claim instance with its verification activities. 

Other requirement examples are requirements that apply to various component categories, all components (**for component**), and all connecitons (**for connection**).

> In the case of *SecurePortConnection* we have a conditional requirement specification. The **when** condition acts as a filter to include the requirement as claim in the assurance case instance only when the **when** predicate holds. This predicate currently operates on declarative model elements which is not supported by Resolute. We have an implementation of this predicate in Java. It can be found in the project *SecurityVerificationJava*.

### Configuring Global Reusable Requirement into an Assurance Case 

Users can configure global requirements into an assurance case in two ways:

- globally by an **assure global** in an assurance plan. This is what we hae done for the example *ApplicationSystem* whose assurance case configuraiton can be found in *alisa/ApplicationSystem.alisa*.

- As a reusable requirement targeted to a specific component. This is done with an **include** statement in a *system requirement set*., Either all requirement of the global requirement set are associated with the component for which the system requirement set is specified, or users can identify individual global requirements. 

### Requirement Refinement and Multiple Verification Activities

Users have the choice of further refining a requirement or specifying multiple verification activities. 

The requirement *SharedBusSecurity* has three verification activities: one verifying security clearance, one verifying information security levels, and one verifying security domains.

A second example is the claim specification for the requirement *SecurePortConnection*, which lists a number of verification activities.

> If there is some reasoning logic involved in these verifications then users can take advantage of the argument logic in verification plans, which supports **all**, **then** conditional, **else** alternative.

## Resolute Utility Libraries

We have three Resolute utility libraries. They contain compute functions and no claim functions. In addition there is a library with Resolute patterns.

### Resolute Patterns

The *Resolute_Patterns* library explains how you can achieve *filter* and *map* operations on collections (Resolute sets and lists) as well as how to flatten nested collections that are the result or recursive traversal of containment or connections.

### Resolute Util

The *Resolute_Util* library complements the *Resolute_Stdlib* library with useful compute functions.

It includes

- missing *is_xyz* functions: is\_abstract, is\_hardware, is\_a\_port\_connection. The latter is a more efficient implementation than the one provided in Resolute_Stdlib.

- functions to return collections of model elements, such as, all threads contained recursively in a component (e.g., a process with thread groups or a whole system), incoming and outgoing connections that can handle port connections as well as data access connections with access rights indicating the direction.

- functions dealing with bindings

- a *remaining\_components* function to perform the set complement (subtraction of one set from another)

- a *is\_before* function to determine the ordering of two elements in a set of strings. This function is used to determine ordering of security clearance and information security level. 

> Note that the *is\_before* can also be used on enumeration literals - properties holding enumeration literals are returned as string and Resolute includes a built-in function to get the set of enumeration literals of an enumeration type as set of string.

- functions for logging model elements and messages as Resolute Result objects (using the claim function mechanism) and debug fucntions to provide a trace in a Resolute console.

### Security Classification Util

The *Security_Classification_Util* library contains functions that are predicates related to security clearance, information security level, security domain, and need to know domain. 

### Security Exposure Util

The *Security\_Exposure\_Util* library provides simple predicate functions related to exposure, authentication, space and time partitioning. 

In addition it provides more complex predicates about contained application components or ports marked as encrypted.

Finally, it provides a set of functions that handle encryption being represented by virtual buses that are tagged with encryption information, i.e., virtual buses are used to represent encrypted channels.

### Security Classification Verification

The *Security\_Classification\_Verification* library contains a collection of claim functions that are used as verification methods. These claim functions deal with

- contained components having consistent security clearance/level

- connected hardware components having consistent security clearance and security domains

- bound components having consistent security clearance and security domains

- consistency of information security level, need to know domains of features in a component, threads inside processes, ends of port connections

### Security Exposure Verification

The *Security\_Exposure\_Verification* library contains a collection of claim functions that are used as verification methods. These claim functions deal with several CWEs identified in the AASPE project. 

They deal with physical exposure, consistent use of encryption for classified information, and consistent handling of unclassified information.

### Design Flaw Verification

The *Design\_Flaw\_Verification* library contains a collection of claim functions that are used as verification methods. These claim functions deal with several CWEs identified in the AASPE project.

 They deal with buffer overflow, inconsistent data specification on input and output, and concurrency issues.
 
### Space Partitioned
 
The *Space\_Partitioned* library contains a collection of claim functions that deal with space partition enforcement for processes. Currently deals with OS and language characteristics. It needs to be expanded to include ARINC653 partitioning.

### Connectivity and Reachability

The *Connectivity\_Reachability* library contains a collection of claim functions that deal with ensuring a full set of connection specifications, as well as functions to provide sets of directly and recursively reachable components, reachable leaf components (components without outgoing connections that are sinks), all connections involved in the reachability of components.

### Vulnerability Impact

The *Vulnerability\_Impact* library currently illustrates one example of identifying a certain type of vulnerability and providing a record of all components that are affected by the vulnerability. Thi sis equivalent to an attack impact analysis demonstrated in the AASPE project.

## Claim Functions vs. Predicate Compute Functions

Users can easily get into the habit of writing everything in Resolute. This results in requirement claims, model traversal to find elements for which to apply verification activities, and the verification methods all in Resolute. When users then register the Resolute method in the verification method registry, develop a verification plans, and specify requirements - they will encounter replication of the same text. 

One way to reduce this replication is to write Resolute compute functions that return a boolean, i.e., *predicate functions*. As they are registered in teh method registry descriptive information is added. This information is then used in the presentation of the assurance case instance. An example is the verification method *AllComponentPortsEncrypted* found in *Security\_Exposure\_Util*.

## Security Example Models

I have one simple *ApplicationSystem* with three connected subsystems and recursively nested threads. For this example, I have an assurance case configuration *ApplicationSystem.alisa* which you can execute in the ALISA Assurance View. 

I have also provided a *Resolute\_Prove\_Configuration* for this model, which pulls together different Resolute functions representing verification methods, such that they can be run within Resolute through a prove statement. I used this to test Resolute claim functions before including them in the assurance case.

The folder *automotive* contains several examples Julien developed in the AASPE project, including a Prius and a Jeep example.  The JeepISOLA model has the **prove** statement. For the Jeep I started a reqspec file that includes some of the security requirements using **include** declarations. (Work in progress).

## Writing Verification Methods and Requirements when Conditions in Java

Requirement when conditions are assumed to be Java methods that accept a single parameter of type EObject.
The parameter actual will be a model element of a declarative AADL model.

Verification methods written in Java have as first parameter an instance model element that is the target of the requirement 
(component instance as specified in the requirement set or a target element within the component as specified by the 'for' in a specific requirement).

See ALISA documentation in OSATE Help for details under the section Guidance for System Verification.

