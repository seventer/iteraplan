grammar IteraQl2;

options {
  language = Java;
}

@header {
  package de.iteratec.iteraplan.elasticeam.iteraql2;
  
  import de.iteratec.iteraplan.elasticeam.iteraql2.qt.*;
  import de.iteratec.iteraplan.elasticeam.iteraql2.qt.predicate.*;
  import de.iteratec.iteraplan.elasticeam.iteraql2.qt.extension.*;
  
  import de.iteratec.iteraplan.elasticeam.operator.filter.predicate.*;
  import de.iteratec.iteraplan.elasticeam.metamodel.ComparisonOperatorExpression;
  import de.iteratec.iteraplan.elasticeam.metamodel.builtin.ComparisonOperators;
}

@lexer::header {
  package de.iteratec.iteraplan.elasticeam.iteraql2;
}

@rulecatch {
	catch (RecognitionException ex) {
		throw new IteraQl2Exception(IteraQl2Exception.PARSER_INVALID_QUERY_STRING, "The entered query is not syntactically correct: " + getErrorMessage(ex, tokenNames));
	}
}

@lexer::rulecatch {
	catch (RecognitionException ex) {
		throw new IteraQl2Exception(IteraQl2Exception.PARSER_INVALID_QUERY_STRING, "The entered query is not syntactically correct: " + getErrorMessage(ex, tokenNames));
	}
}

@members {
	public void displayRecognitionError(String[] tokenNames,
                                        RecognitionException ex) {
		//Overrides the standard method, so that recovery does not occur.
    throw new IteraQl2Exception(IteraQl2Exception.PARSER_INVALID_QUERY_STRING, "The entered query is not syntactically correct: " + getErrorMessage(ex, tokenNames));
  }
    
 	public void reportError(RecognitionException ex) {
		throw new IteraQl2Exception(IteraQl2Exception.PARSER_INVALID_QUERY_STRING, "The entered query is not syntactically correct: " + getErrorMessage(ex, tokenNames));
	}
}

@lexer::members{

	public void displayRecognitionError(String[] tokenNames,
                                        RecognitionException ex) {
		//Overrides the standard method, so that recovery does not occur.
    throw new IteraQl2Exception(IteraQl2Exception.PARSER_INVALID_QUERY_STRING, "The entered query is not syntactically correct: " + getErrorMessage(ex, tokenNames));
  }
}

query returns [Query query]
	: {
			RelationshipEnd relationshipEnd = null; 
		}
		ut=universalType 
		(
			WSSEQ? rEnd=relationshipEnd
			{
				relationshipEnd = $rEnd.relEnd;
			}
		)? WSSEQ? SEMICOLON
		{
			$query = new Query($ut.universalType, relationshipEnd);
		}
	;

universalType returns [UniversalType universalType]
	: {
			List<Extension> extensions = new ArrayList<Extension>();
		}
		utName=NAME
		(
			WSSEQ? ext=universalTypeExtension
			{
				extensions.add($ext.extension);
			}
		)*
		{
			$universalType = new UniversalType($utName.text.trim(), extensions);
		}
	;
	
universalTypeExtension returns [Extension extension]
	: (
		FULL_STOP KEYWORD_POWER LEFT_PARENTHESIS WSSEQ? RIGHT_PARENTHESIS
			{
				$extension = new PowerExtension();
			}
		)
	| (
		FULL_STOP KEYWORD_NULLIFY LEFT_PARENTHESIS WSSEQ? RIGHT_PARENTHESIS
			{
				$extension = new NullifyExtension(null);
			}
		)
	| (
		ext=extension
			{
				$extension = $ext.extension;
			}
		)
	;
	
relationshipEnd returns [RelationshipEnd relEnd]
	: (
			{
				List<Extension> extensions = new ArrayList<Extension>();
				RelationshipEnd nextRelEnd = null;
			}
		SLASH endName=NAME 
			(
				WSSEQ? cExt=relationshipEndExtension
					{
						extensions.add($cExt.extension);
					}
			)*  
			(
				WSSEQ? nextEnd=relationshipEnd
					{
						nextRelEnd = $nextEnd.relEnd;
					}
			)?
			{
				$relEnd = new SimpleRelationshipEnd($endName.text.trim(), extensions, nextRelEnd);
			}
		)
	| (
			{
				RelationshipEnd nextRelEnd = null;
				List<Extension> extensions = new ArrayList<Extension>();
			}
		FULL_STOP KEYWORD_UNFOLD LEFT_PARENTHESIS WSSEQ? kRelEnd=relationshipEnd WSSEQ? RIGHT_PARENTHESIS 
			(
				WSSEQ? cExt=relationshipEndExtension
					{
						extensions.add($cExt.extension);
					}
			)*
			(
				WSSEQ? nRelEnd=relationshipEnd
				{
					nextRelEnd = $nRelEnd.relEnd;
				}
			)?
			{
				$relEnd = new UnfoldQueryRelationshipEnd($kRelEnd.relEnd, extensions, nextRelEnd);
			}
		)
	;
	
relationshipEndExtension returns [Extension extension]
	: (
		FULL_STOP KEYWORD_NULLIFY LEFT_PARENTHESIS WSSEQ? nRelEnd=relationshipEnd WSSEQ? RIGHT_PARENTHESIS
			{
				$extension = new NullifyExtension($nRelEnd.relEnd);
			}
		) 
	| (
		ext=extension
			{
				$extension = $ext.extension;
			}
		)
	;
	
property returns [PredicateLeafProperty prop]
	: (
		AT_SIGN identifier=NAME
			{
				$prop = new CanonicLeafProperty($identifier.text.trim());
			}
		) 
	| (
		KEYWORD_FOLD_LEVEL LEFT_PARENTHESIS WSSEQ? kRelEnd=relationshipEnd RIGHT_PARENTHESIS
			{
				$prop = new FoldLevelDerivedLeafProperty($kRelEnd.relEnd);
			}
		)
	| (
		KEYWORD_COUNT LEFT_PARENTHESIS WSSEQ? f=feature RIGHT_PARENTHESIS
		{
				$prop = new CountDerivedLeafProperty($f.feature);
		}
		)
	| (
		KEYWORD_VIEW LEFT_PARENTHESIS WSSEQ? vRelEnd=relationshipEnd WSSEQ? p=property WSSEQ? RIGHT_PARENTHESIS
		{
				$prop = new ViewDerivedLeafProperty($vRelEnd.relEnd, $p.prop);
		}
		)
	;
	
feature returns [Feature feature]
	: (
		rEnd=relationshipEnd
			{
				$feature = $rEnd.relEnd;
			}
		)
	| (
		p=property
			{
				$feature = $p.prop;
			}
		)
	;

predicate returns [Predicate pred]
	: 
	(
	    {
	      Predicate boolPred = null;
	      CompositePredicateOperation boolOp = null;
	      
	    }
	    LEFT_PARENTHESIS WSSEQ? sPred=predicate RIGHT_PARENTHESIS WSSEQ? 
	      (
	        bOpPred=booleanOperationPredicate
	        {
	          boolPred = $bOpPred.pred;
	          boolOp = $bOpPred.op;
	        }
	      )? 
			{
			  if (boolPred != null) {			  
			    pred = new PredicateNode($sPred.pred, boolPred, boolOp);
			  } else {
			    pred = new PredicateNode($sPred.pred, null, CompositePredicateOperation.ENCLOSE);
			  }
			} 
		)
		| (
		  subPred=basePredicate cPred=compositePredicate?
			{
				if (cPred != null) {
					$pred = new PredicateNode($subPred.pred, $cPred.pred, $cPred.op);
				} else {
					$pred = $subPred.pred;
				}
			}
		)
	| (
	  not=notPredicate
	    {
	      $pred = new PredicateNode($not.pred, null, $not.op);
	    }
	  )
	;
	
booleanOperationPredicate returns [Predicate pred, CompositePredicateOperation op]
  : (
      composite=compositePredicate
      {
        $pred = $composite.pred;
        $op = $composite.op;
      }
    )
    | ( 
      not=notPredicate
      {
        $pred = $not.pred;
        $op = $not.op;
      }
    )
  ;
  
notPredicate returns [Predicate pred, CompositePredicateOperation op]
  : (
    EXCLAMATION_MARK WSSEQ? subPred=predicate 
      {
      $pred = $subPred.pred;
      $op = CompositePredicateOperation.NOT;
      }
    )
  ;
	
compositePredicate returns [Predicate pred, CompositePredicateOperation op]
	:	(
		AMPERSAND WSSEQ? p=predicate 
			{
				$pred=$p.pred; $op=CompositePredicateOperation.AND;
			}
		)
	| (
		VERTICAL_BAR WSSEQ? p=predicate 
			{
				$pred=$p.pred; $op=CompositePredicateOperation.OR;
			}
		)
	;
	
basePredicate returns [PredicateLeaf pred]
	: p=property ext=predicateExtension
		{
			$pred = new PredicateLeaf($p.prop, $ext.op, $ext.value);
		}
	;
	
predicateExtension returns [ComparisonOperatorExpression op, PredicateLeafReferenceValue value]
	: (
		bOp=bOperation {$op=bOp;} (
			(
				p=property
				{
					$value = new PropertyAsReferenceValue($p.prop);
				}
			) | (
				pVal=predicateValue
				{
					$value = new SimpleReferenceValue($pVal.value);
				}
			) WSSEQ?
			)
		)
	| (
		WSSEQ? FULL_STOP KEYWORD_BEGINS_WITH LEFT_PARENTHESIS WSSEQ? bVal=stringPropertyValue WSSEQ? RIGHT_PARENTHESIS WSSEQ?
			{
				$op = ComparisonOperators.BEGINS_WITH;
				$value = $bVal.value;
			}
		)
	| (
		WSSEQ? FULL_STOP KEYWORD_ENDS_WITH LEFT_PARENTHESIS WSSEQ? eVal=stringPropertyValue WSSEQ? RIGHT_PARENTHESIS WSSEQ?
			{
				$op = ComparisonOperators.ENDS_WITH;
				$value = $eVal.value;
			}
		)
	| (
		WSSEQ? FULL_STOP KEYWORD_CONTAINS LEFT_PARENTHESIS WSSEQ? cVal=stringPropertyValue WSSEQ? RIGHT_PARENTHESIS WSSEQ?
			{
				$op = ComparisonOperators.CONTAINS;
				$value = $cVal.value;
			}
		)
	;
	
predicateValue returns [String value]
	: (
		tVal=predicateValueToken 
			{
				$value=$tVal.text.trim();
			} 
		)
	| (
		val=STRVALUE 
			{
				$value=$val.text.trim().substring(1,($val.text.trim().length()-1));
			}
		)
	;
	
//Simulates a token. This is necessary, since defining the real token allows it to match the
//NAME token as well, which itself causes the parser to fail.
predicateValueToken returns [String val]
  : {
      String result = null;
    }
    (
      baseName=NAME
      {
        result = $baseName.text;
      }
      (
        fs=FULL_STOP aName=NAME
        {
          result = result + $fs.text + $aName.text;
        }
      )*
    )
    {
      $val = result;
    }
  ;


stringPropertyValue returns [PredicateLeafReferenceValue value]
	: (
		p=property
			{
				$value = new PropertyAsReferenceValue($p.prop);
			}
		) 
	| (
		val=STRVALUE
			{
				$value = new SimpleReferenceValue($val.text.trim().substring(1,($val.text.trim().length()-1)));
			}
		)
	;

bOperation returns [ComparisonOperatorExpression op]
	: WSSEQ? (
	( LESS_THAN {$op = ComparisonOperators.LESS;})
	| ( GREATER_THAN {$op = ComparisonOperators.GREATER;} )
	| ( LESS_THAN EQUALS {$op = ComparisonOperators.LESS_EQUALS;} )
	| ( GREATER_THAN EQUALS {$op = ComparisonOperators.GREATER_EQUALS;} )
	| ( EQUALS {$op = ComparisonOperators.EQUALS;} )
	| ( EXCLAMATION_MARK EQUALS {$op = ComparisonOperators.NOT_EQUALS;} )
	) WSSEQ?
	;
	
extension returns [Extension extension]
	: (
		{
			List<MoveExtensionProperty> props = new ArrayList<MoveExtensionProperty>();
		}
		LEFT_CURLY_BRACKET 
			(
					{
						String identifier = null;
					}
				(
		 		newName=NAME EQUALS WSSEQ?
		 			{
		 				identifier = $newName.text.trim();
		 			}
		 		)? p=property
		 		{
		 			props.add(new MoveExtensionProperty(identifier, $p.prop));
		 		}
			)+ WSSEQ? RIGHT_CURLY_BRACKET
			{
				$extension = new MoveExtension(props);
			}
		) 
	| (
		FULL_STOP KEYWORD_CLUSTER LEFT_PARENTHESIS WSSEQ? kRelEnd=relationshipEnd WSSEQ? RIGHT_PARENTHESIS
			{
				$extension = new ClusterExtension($kRelEnd.relEnd);
			}
		)  
	| (
		FULL_STOP KEYWORD_OBJECTIFY LEFT_PARENTHESIS WSSEQ? p=property WSSEQ? RIGHT_PARENTHESIS
			{
				$extension = new ObjectifyExtension($p.prop);
			}
		) 
	| (
		{
			Extension nextExtension = null;
		}
		LEFT_SQUARE_BRACKET WSSEQ? pred=predicate RIGHT_SQUARE_BRACKET 
			{
				$extension = new PredicateExtension($pred.pred);
			}
		)
	| (
		{
			Extension nextExtension = null;
		}
		FULL_STOP KEYWORD_EXPAND LEFT_PARENTHESIS WSSEQ? expEnd=relationshipEnd WSSEQ? RIGHT_PARENTHESIS 
			{
				$extension = new ExpandExtension($expEnd.relEnd);
			}
		)
	;
	
	
//Tokens and fragments	
LEFT_CURLY_BRACKET
	: '\u007B'
	;
	
RIGHT_CURLY_BRACKET
	:	'\u007D'
	;

SINGLE_QUOTATION
	: '\u0027'
	;
	
DOUBLE_QUOTATION
	: '\u0022'
	;
	
LEFT_SQUARE_BRACKET
	: '\u005B'
	;
	
SLASH
	: '\u002F'
	;
	
FULL_STOP
	: '\u002E'
	;
	
LEFT_PARENTHESIS
	: '\u0028'
	;
	
AT_SIGN
	: '\u0040'
	;
	
SEMICOLON
	: '\u003B'
	;
	
RIGHT_PARENTHESIS
	: '\u0029'
	;
	
RIGHT_SQUARE_BRACKET
	: '\u005D'
	;
	
AMPERSAND
	: '\u0026'
	;

EXCLAMATION_MARK
	: '\u0021'
	;
	
LESS_THAN
	: '\u003C'
	;
	
EQUALS
	: '\u003D'
	;
	
GREATER_THAN
	: '\u003E'
	;
	
VERTICAL_BAR
	: '\u007C'
	;
	
KEYWORD_EXPAND
	: 'expand'
	;
	
KEYWORD_POWER
	: 'power'
	;
	
KEYWORD_UNFOLD
	: 'unfold'
	;
	
KEYWORD_FOLD_LEVEL
	: (WHITESPACE_CHAR)* 'foldLevel'
	;
	
KEYWORD_COUNT
	: (WHITESPACE_CHAR)* 'count'
	;
	
KEYWORD_VIEW
	: (WHITESPACE_CHAR)* 'view'
	;
	
KEYWORD_BEGINS_WITH
	: 'beginsWith'
	;
	
KEYWORD_ENDS_WITH
	: 'endsWith'
	;
	
KEYWORD_CONTAINS
	: 'contains'
	;
	
KEYWORD_CLUSTER
	: 'cluster'
	;
	
KEYWORD_NULLIFY
	: 'nullify'
	;
	
KEYWORD_OBJECTIFY
	: 'objectify'
	;
	
//Unicode whitespace characters (see http://en.wikipedia.org/wiki/Whitespace_character)
fragment WHITESPACE_CHAR
	: '\u0009' 
	| '\u000A'..'\u000D' 
	| '\u0020' 
	| '\u0085' 
	| '\u00A0' 
	| '\u1680' 
	| '\u180E' 
	| '\u2000'..'\u200A' 
	| '\u2028' 
	| '\u2029' 
	| '\u202F' 
	| '\u205F' 
	| '\u3000'
	;
	
//characters not allowed in the name of an elasticeam entity (see http://en.wikipedia.org/wiki/C0_Controls_and_Basic_Latin)
fragment CONTROL_CHAR
	: LEFT_SQUARE_BRACKET
	| SLASH
	| FULL_STOP
	| LEFT_PARENTHESIS
	| AT_SIGN
	| SINGLE_QUOTATION
	| DOUBLE_QUOTATION
	| SEMICOLON
	| RIGHT_PARENTHESIS
	| RIGHT_SQUARE_BRACKET
	| AMPERSAND
	| EXCLAMATION_MARK
	| LESS_THAN
	| EQUALS
	| GREATER_THAN
	| VERTICAL_BAR
	| LEFT_CURLY_BRACKET
	| RIGHT_CURLY_BRACKET
	;
	
fragment WORD_CHAR
	: ~(WHITESPACE_CHAR | CONTROL_CHAR)
	;
	
NAME
	: WHITESPACE_CHAR* ((WORD_CHAR (WORD_CHAR | WHITESPACE_CHAR)* WORD_CHAR) | WORD_CHAR) WHITESPACE_CHAR*
	;
	
//any string in single or double quotation marks
STRVALUE
	: WHITESPACE_CHAR* ((DOUBLE_QUOTATION (~(DOUBLE_QUOTATION|'\\'))* DOUBLE_QUOTATION) | (SINGLE_QUOTATION (~(SINGLE_QUOTATION|'\\'))* SINGLE_QUOTATION)) WHITESPACE_CHAR*
	;

//Whitespace sequence
WSSEQ
	: (WHITESPACE_CHAR)*
	;

