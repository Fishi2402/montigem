# AST Integration

This experiment loads the resulting AST of a MontiCore grammar into a MontiGem domain.

The various sub-projects:

## DSL

Defines a simple DSL & builds it using the MontiCore Generator

## DSL2CD

Parses the (reported) CDs of the DSL and converts it into a MontiGem CD:

* shortens the packages names
* Generates an AST-to-Domain Translation class
* ...

 Grammar/AST-Type  | Domain-Type                        | o                                                                                |
-------------------|------------------------------------|----------------------------------------------------------------------------------
 P = ...           | class ASTP { ... }                 | Every class production results in a class                                        
 named:Prod        | public Prod named;                 | Simple references are turned into attributes                                     
 named:Prod*       | ->(nameds) Prod [*];               | stars result in [*] assocs                                                       
 named:Prod?       | ->(names) Prod [0..1];             | opts result in [0..1] assocs                                                     
 named:Prod+       | ->(nameds) Prod [*];               | plus result in [*] assocs (limitation of the reported CDs)                       
 List<String>      | joined String                      | no overview page exists for List of Strings                                      
 interface Expr    | class Expr {} interface IExpr {  } | workaround until typecheck arrives in CD2GUI (no overviews exist for interfaces) 
 P impl I          | class Expr { Optional<P> p  }      | Typecheck workaround                                                             
 scopes            | --skipped                          | Not implemented as of now                                                        
 symbols           | --skipped                          | Not implemented as of now                                                        
 symbol references | --skipped                          | Not implemented as of now                                                        

Requires the CDs of component grammars to be present (which as of now, are hand-copied).

## backend

A MontiGem backend project,
in which the DSL2CD-domain-CD and a hand-written CD are merged.

## frontend

A MontiGem frontend project,
in which the merged CD is used along with CD2GUI.

The [ParseMyDSL](http://localhost:4200/gui/ParseMyDSL) page is a hand-written page to parse a model,
shows any errors,
and as an entry-point to CD2GUI. 
