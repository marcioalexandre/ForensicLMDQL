// This class is generated. Do NOT modify it, or
// add it to source control.

package mondrian.resource;
import java.io.IOException;
import java.util.Locale;

/**
 * This class was generated
 * by class org.eigenbase.resgen.ResourceGen
 * from C:/work/mondrian/src/mondrian/resource/MondrianResource.xml
 * on Sat Oct 25 23:05:15 BRT 2008.
 * It contains a list of messages, and methods to
 * retrieve and format those messages.
 */

public class MondrianResource extends org.eigenbase.resgen.ShadowResourceBundle {
    public MondrianResource() throws IOException {
    }
    private static final String baseName = "mondrian.resource.MondrianResource";
    /**
     * Retrieves the singleton instance of {@link MondrianResource}. If
     * the application has called {@link #setThreadLocale}, returns the
     * resource for the thread's locale.
     */
    public static synchronized MondrianResource instance() {
        return (MondrianResource)instance(baseName);
    }
    /**
     * Retrieves the instance of {@link MondrianResource} for the given locale.
     */
    public static synchronized MondrianResource instance(Locale locale) {
        return (MondrianResource) instance(baseName, locale);
    }

    /**
     * <code>Internal</code> is 'Internal error: {0}'
     */
    public final _Def0 Internal = new _Def0("Internal", "Internal error: {0}", null);

    /**
     * <code>MdxCubeNotFound</code> is 'MDX cube ''{0}'' not found'
     */
    public final _Def0 MdxCubeNotFound = new _Def0("MdxCubeNotFound", "MDX cube ''{0}'' not found", null);

    /**
     * <code>MdxChildObjectNotFound</code> is 'MDX object ''{0}'' not found in {1}'
     */
    public final _Def1 MdxChildObjectNotFound = new _Def1("MdxChildObjectNotFound", "MDX object ''{0}'' not found in {1}", null);

    /**
     * <code>MemberNotFound</code> is 'Member ''{0}'' not found'
     */
    public final _Def0 MemberNotFound = new _Def0("MemberNotFound", "Member ''{0}'' not found", null);

    /**
     * <code>MdxCubeName</code> is 'cube ''{0}'''
     */
    public final _Def2 MdxCubeName = new _Def2("MdxCubeName", "cube ''{0}''", null);

    /**
     * <code>MdxHierarchyName</code> is 'hierarchy ''{0}'''
     */
    public final _Def2 MdxHierarchyName = new _Def2("MdxHierarchyName", "hierarchy ''{0}''", null);

    /**
     * <code>MdxDimensionName</code> is 'dimension ''{0}'''
     */
    public final _Def2 MdxDimensionName = new _Def2("MdxDimensionName", "dimension ''{0}''", null);

    /**
     * <code>MdxLevelName</code> is 'level ''{0}'''
     */
    public final _Def2 MdxLevelName = new _Def2("MdxLevelName", "level ''{0}''", null);

    /**
     * <code>MdxMemberName</code> is 'member ''{0}'''
     */
    public final _Def2 MdxMemberName = new _Def2("MdxMemberName", "member ''{0}''", null);

    /**
     * <code>WhileParsingMdx</code> is 'Error while parsing MDX statement ''{0}'''
     */
    public final _Def0 WhileParsingMdx = new _Def0("WhileParsingMdx", "Error while parsing MDX statement ''{0}''", null);

    /**
     * <code>WhileParsingMdxExpression</code> is 'Syntax error in MDX expression ''{0}'''
     */
    public final _Def0 WhileParsingMdxExpression = new _Def0("WhileParsingMdxExpression", "Syntax error in MDX expression ''{0}''", null);

    /**
     * <code>MdxFatalError</code> is 'MDX parser cannot recover from previous error(s)'
     */
    public final _Def3 MdxFatalError = new _Def3("MdxFatalError", "MDX parser cannot recover from previous error(s)", null);

    /**
     * <code>FailedToParseQuery</code> is 'Failed to parse query ''{0}'''
     */
    public final _Def0 FailedToParseQuery = new _Def0("FailedToParseQuery", "Failed to parse query ''{0}''", null);

    /**
     * <code>MdxError</code> is 'Error: {0}'
     */
    public final _Def0 MdxError = new _Def0("MdxError", "Error: {0}", null);

    /**
     * <code>MdxSyntaxError</code> is 'Syntax error at token ''{0}'''
     */
    public final _Def0 MdxSyntaxError = new _Def0("MdxSyntaxError", "Syntax error at token ''{0}''", null);

    /**
     * <code>MdxSyntaxErrorAt</code> is 'Syntax error at line {1}, column {2}, token ''{0}'''
     */
    public final _Def4 MdxSyntaxErrorAt = new _Def4("MdxSyntaxErrorAt", "Syntax error at line {1}, column {2}, token ''{0}''", null);

    /**
     * <code>MdxFatalSyntaxError</code> is 'Couldn''t repair and continue parse'
     */
    public final _Def5 MdxFatalSyntaxError = new _Def5("MdxFatalSyntaxError", "Couldn''t repair and continue parse", null);

    /**
     * <code>MdxCubeSlicerMemberError</code> is 'Failed to add Cube Slicer with member ''{0}'' for hierarchy ''{1}'' on cube ''{2}'''
     */
    public final _Def4 MdxCubeSlicerMemberError = new _Def4("MdxCubeSlicerMemberError", "Failed to add Cube Slicer with member ''{0}'' for hierarchy ''{1}'' on cube ''{2}''", null);

    /**
     * <code>MdxCubeSlicerHierarchyError</code> is 'Failed to add Cube Slicer for hierarchy ''{0}'' on cube ''{1}'''
     */
    public final _Def1 MdxCubeSlicerHierarchyError = new _Def1("MdxCubeSlicerHierarchyError", "Failed to add Cube Slicer for hierarchy ''{0}'' on cube ''{1}''", null);

    /**
     * <code>MdxInvalidMember</code> is 'Invalid member identifier ''{0}'''
     */
    public final _Def0 MdxInvalidMember = new _Def0("MdxInvalidMember", "Invalid member identifier ''{0}''", null);

    /**
     * <code>MdxCalculatedHierarchyError</code> is 'Hierarchy for calculated member ''{0}'' not found'
     */
    public final _Def0 MdxCalculatedHierarchyError = new _Def0("MdxCalculatedHierarchyError", "Hierarchy for calculated member ''{0}'' not found", null);

    /**
     * <code>MdxAxisIsNotSet</code> is 'Axis ''{0}'' expression is not a set'
     */
    public final _Def0 MdxAxisIsNotSet = new _Def0("MdxAxisIsNotSet", "Axis ''{0}'' expression is not a set", null);

    /**
     * <code>MdxMemberExpIsSet</code> is 'Member expression ''{0}'' must not be a set'
     */
    public final _Def0 MdxMemberExpIsSet = new _Def0("MdxMemberExpIsSet", "Member expression ''{0}'' must not be a set", null);

    /**
     * <code>MdxSetExpNotSet</code> is 'Set expression ''{0}'' must be a set'
     */
    public final _Def0 MdxSetExpNotSet = new _Def0("MdxSetExpNotSet", "Set expression ''{0}'' must be a set", null);

    /**
     * <code>MdxFuncArgumentsNum</code> is 'Function ''{0}'' must have at least 2 arguments'
     */
    public final _Def0 MdxFuncArgumentsNum = new _Def0("MdxFuncArgumentsNum", "Function ''{0}'' must have at least 2 arguments", null);

    /**
     * <code>MdxFuncNotHier</code> is 'Argument ''{0,number}'' of function ''{1}'' must be a hierarchy'
     */
    public final _Def6 MdxFuncNotHier = new _Def6("MdxFuncNotHier", "Argument ''{0,number}'' of function ''{1}'' must be a hierarchy", null);

    /**
     * <code>UnknownParameter</code> is 'Unknown parameter ''{0}'''
     */
    public final _Def0 UnknownParameter = new _Def0("UnknownParameter", "Unknown parameter ''{0}''", null);

    /**
     * <code>MdxFormulaNotFound</code> is 'Calculated {0} ''{1}'' has not been found in query ''{2}'''
     */
    public final _Def4 MdxFormulaNotFound = new _Def4("MdxFormulaNotFound", "Calculated {0} ''{1}'' has not been found in query ''{2}''", null);

    /**
     * <code>MdxCantFindMember</code> is 'Cannot find MDX member ''{0}''. Make sure it is indeed a member and not a level or a hierarchy.'
     */
    public final _Def0 MdxCantFindMember = new _Def0("MdxCantFindMember", "Cannot find MDX member ''{0}''. Make sure it is indeed a member and not a level or a hierarchy.", null);

    /**
     * <code>CalculatedMember</code> is 'calculated member'
     */
    public final _Def5 CalculatedMember = new _Def5("CalculatedMember", "calculated member", null);

    /**
     * <code>CalculatedSet</code> is 'calculated set'
     */
    public final _Def5 CalculatedSet = new _Def5("CalculatedSet", "calculated set", null);

    /**
     * <code>MdxCalculatedFormulaUsedOnAxis</code> is 'Cannot delete {0} ''{1}''. It is used on {2} axis.'
     */
    public final _Def4 MdxCalculatedFormulaUsedOnAxis = new _Def4("MdxCalculatedFormulaUsedOnAxis", "Cannot delete {0} ''{1}''. It is used on {2} axis.", null);

    /**
     * <code>MdxCalculatedFormulaUsedOnSlicer</code> is 'Cannot delete {0} ''{1}''. It is used on slicer.'
     */
    public final _Def1 MdxCalculatedFormulaUsedOnSlicer = new _Def1("MdxCalculatedFormulaUsedOnSlicer", "Cannot delete {0} ''{1}''. It is used on slicer.", null);

    /**
     * <code>MdxCalculatedFormulaUsedInFormula</code> is 'Cannot delete {0} ''{1}''. It is used in definition of {2} ''{3}''.'
     */
    public final _Def7 MdxCalculatedFormulaUsedInFormula = new _Def7("MdxCalculatedFormulaUsedInFormula", "Cannot delete {0} ''{1}''. It is used in definition of {2} ''{3}''.", null);

    /**
     * <code>MdxCalculatedFormulaUsedInQuery</code> is 'Cannot delete {0} ''{1}''. It is used in query ''{2}''.'
     */
    public final _Def4 MdxCalculatedFormulaUsedInQuery = new _Def4("MdxCalculatedFormulaUsedInQuery", "Cannot delete {0} ''{1}''. It is used in query ''{2}''.", null);

    /**
     * <code>MdxAxisShowSubtotalsNotSupported</code> is 'Show/hide subtotals operation on axis ''{0,number}'' is not supported.'
     */
    public final _Def8 MdxAxisShowSubtotalsNotSupported = new _Def8("MdxAxisShowSubtotalsNotSupported", "Show/hide subtotals operation on axis ''{0,number}'' is not supported.", null);

    /**
     * <code>NoFunctionMatchesSignature</code> is 'No function matches signature ''{0}'''
     */
    public final _Def0 NoFunctionMatchesSignature = new _Def0("NoFunctionMatchesSignature", "No function matches signature ''{0}''", null);

    /**
     * <code>MoreThanOneFunctionMatchesSignature</code> is 'More than one function matches signature ''{0}'''
     */
    public final _Def0 MoreThanOneFunctionMatchesSignature = new _Def0("MoreThanOneFunctionMatchesSignature", "More than one function matches signature ''{0}''", null);

    /**
     * <code>MemberNotInLevelHierarchy</code> is 'The member ''{0}'' is not in the same hierarchy as the level ''{1}''.'
     */
    public final _Def1 MemberNotInLevelHierarchy = new _Def1("MemberNotInLevelHierarchy", "The member ''{0}'' is not in the same hierarchy as the level ''{1}''.", null);

    /**
     * <code>ToggleDrillStateRecursiveNotSupported</code> is '''RECURSIVE'' is not supported in ToggleDrillState.'
     */
    public final _Def3 ToggleDrillStateRecursiveNotSupported = new _Def3("ToggleDrillStateRecursiveNotSupported", "''RECURSIVE'' is not supported in ToggleDrillState.", null);

    /**
     * <code>CompoundSlicer</code> is 'WHERE clause expression returned set with more than one element.'
     */
    public final _Def3 CompoundSlicer = new _Def3("CompoundSlicer", "WHERE clause expression returned set with more than one element.", null);

    /**
     * <code>EmptySlicer</code> is 'WHERE clause expression returned NULL or empty set.'
     */
    public final _Def3 EmptySlicer = new _Def3("EmptySlicer", "WHERE clause expression returned NULL or empty set.", null);

    /**
     * <code>FunctionMbrAndLevelHierarchyMismatch</code> is 'The <level> and <member> arguments to {0} must be from the same hierarchy. The level was from ''{1}'' but the member was from ''{2}''.'
     */
    public final _Def4 FunctionMbrAndLevelHierarchyMismatch = new _Def4("FunctionMbrAndLevelHierarchyMismatch", "The <level> and <member> arguments to {0} must be from the same hierarchy. The level was from ''{1}'' but the member was from ''{2}''.", null);

    /**
     * <code>CousinHierarchyMismatch</code> is 'The member arguments to the Cousin function must be from the same hierarchy. The members are ''{0}'' and ''{1}''.'
     */
    public final _Def1 CousinHierarchyMismatch = new _Def1("CousinHierarchyMismatch", "The member arguments to the Cousin function must be from the same hierarchy. The members are ''{0}'' and ''{1}''.", null);

    /**
     * <code>DimensionInIndependentAxes</code> is 'Dimension ''{0}'' appears in more than one independent axis.'
     */
    public final _Def0 DimensionInIndependentAxes = new _Def0("DimensionInIndependentAxes", "Dimension ''{0}'' appears in more than one independent axis.", null);

    /**
     * <code>ArgsMustHaveSameHierarchy</code> is 'All arguments to function ''{0}'' must have same hierarchy.'
     */
    public final _Def0 ArgsMustHaveSameHierarchy = new _Def0("ArgsMustHaveSameHierarchy", "All arguments to function ''{0}'' must have same hierarchy.", null);

    /**
     * <code>TimeArgNeeded</code> is 'Argument to function ''{0}'' must belong to Time hierarchy.'
     */
    public final _Def0 TimeArgNeeded = new _Def0("TimeArgNeeded", "Argument to function ''{0}'' must belong to Time hierarchy.", null);

    /**
     * <code>InvalidAxis</code> is 'Invalid axis specification. The axis number must be an integer between 0 and {0,number}, but it was {1,number}.'
     */
    public final _Def9 InvalidAxis = new _Def9("InvalidAxis", "Invalid axis specification. The axis number must be an integer between 0 and {0,number}, but it was {1,number}.", null);

    /**
     * <code>DuplicateAxis</code> is 'Duplicate axis name ''{0}''.'
     */
    public final _Def0 DuplicateAxis = new _Def0("DuplicateAxis", "Duplicate axis name ''{0}''.", null);

    /**
     * <code>DupDimensionsInTuple</code> is 'Tuple contains more than one member of dimension ''{0}''.'
     */
    public final _Def0 DupDimensionsInTuple = new _Def0("DupDimensionsInTuple", "Tuple contains more than one member of dimension ''{0}''.", null);

    /**
     * <code>VisualTotalsAppliedToTuples</code> is 'Argument to ''VisualTotals'' function must be a set of members; got set of tuples.'
     */
    public final _Def3 VisualTotalsAppliedToTuples = new _Def3("VisualTotalsAppliedToTuples", "Argument to ''VisualTotals'' function must be a set of members; got set of tuples.", null);

    /**
     * <code>ParameterIsNotModifiable</code> is 'Parameter ''{0}'' (defined at ''{1}'' scope) is not modifiable'
     */
    public final _Def1 ParameterIsNotModifiable = new _Def1("ParameterIsNotModifiable", "Parameter ''{0}'' (defined at ''{1}'' scope) is not modifiable", null);

    /**
     * <code>ParameterDefinedMoreThanOnce</code> is 'Parameter ''{0}'' is defined more than once in this statement'
     */
    public final _Def0 ParameterDefinedMoreThanOnce = new _Def0("ParameterDefinedMoreThanOnce", "Parameter ''{0}'' is defined more than once in this statement", null);

    /**
     * <code>CycleDuringParameterEvaluation</code> is 'Cycle occurred while evaluating parameter ''{0}'''
     */
    public final _Def0 CycleDuringParameterEvaluation = new _Def0("CycleDuringParameterEvaluation", "Cycle occurred while evaluating parameter ''{0}''", null);

    /**
     * <code>CastInvalidType</code> is 'Unknown type ''{0}''; values are NUMERIC, STRING, BOOLEAN'
     */
    public final _Def0 CastInvalidType = new _Def0("CastInvalidType", "Unknown type ''{0}''; values are NUMERIC, STRING, BOOLEAN", null);

    /**
     * <code>NullNotSupported</code> is 'Function does not support NULL member parameter'
     */
    public final _Def3 NullNotSupported = new _Def3("NullNotSupported", "Function does not support NULL member parameter", null);

    /**
     * <code>TwoNullsNotSupported</code> is 'Function does not support two NULL member parameters'
     */
    public final _Def3 TwoNullsNotSupported = new _Def3("TwoNullsNotSupported", "Function does not support two NULL member parameters", null);

    /**
     * <code>NoTimeDimensionInCube</code> is 'Cannot use the function ''{0}'', no time dimension is available for this cube.'
     */
    public final _Def0 NoTimeDimensionInCube = new _Def0("NoTimeDimensionInCube", "Cannot use the function ''{0}'', no time dimension is available for this cube.", null);

    /**
     * <code>ConnectStringMandatoryProperties</code> is 'Connect string must contain property ''{0}'' or property ''{1}'''
     */
    public final _Def1 ConnectStringMandatoryProperties = new _Def1("ConnectStringMandatoryProperties", "Connect string must contain property ''{0}'' or property ''{1}''", null);

    /**
     * <code>NonTimeLevelInTimeHierarchy</code> is 'Level ''{0}'' belongs to a time hierarchy, so its level-type must be  ''Years'', ''Quarters'', ''Months'', ''Weeks'' or ''Days''.'
     */
    public final _Def0 NonTimeLevelInTimeHierarchy = new _Def0("NonTimeLevelInTimeHierarchy", "Level ''{0}'' belongs to a time hierarchy, so its level-type must be  ''Years'', ''Quarters'', ''Months'', ''Weeks'' or ''Days''.", null);

    /**
     * <code>TimeLevelInNonTimeHierarchy</code> is 'Level ''{0}'' does not belong to a time hierarchy, so its level-type must be ''Standard''.'
     */
    public final _Def0 TimeLevelInNonTimeHierarchy = new _Def0("TimeLevelInNonTimeHierarchy", "Level ''{0}'' does not belong to a time hierarchy, so its level-type must be ''Standard''.", null);

    /**
     * <code>MustSpecifyPrimaryKeyForHierarchy</code> is 'In usage of hierarchy ''{0}'' in cube ''{1}'', you must specify a primary key.'
     */
    public final _Def1 MustSpecifyPrimaryKeyForHierarchy = new _Def1("MustSpecifyPrimaryKeyForHierarchy", "In usage of hierarchy ''{0}'' in cube ''{1}'', you must specify a primary key.", null);

    /**
     * <code>MustSpecifyPrimaryKeyTableForHierarchy</code> is 'Must specify a primary key table for hierarchy ''{0}'', because it has more than one table.'
     */
    public final _Def0 MustSpecifyPrimaryKeyTableForHierarchy = new _Def0("MustSpecifyPrimaryKeyTableForHierarchy", "Must specify a primary key table for hierarchy ''{0}'', because it has more than one table.", null);

    /**
     * <code>MustSpecifyForeignKeyForHierarchy</code> is 'In usage of hierarchy ''{0}'' in cube ''{1}'', you must specify a foreign key, because the hierarchy table is different from the fact table.'
     */
    public final _Def1 MustSpecifyForeignKeyForHierarchy = new _Def1("MustSpecifyForeignKeyForHierarchy", "In usage of hierarchy ''{0}'' in cube ''{1}'', you must specify a foreign key, because the hierarchy table is different from the fact table.", null);

    /**
     * <code>LevelMustHaveNameExpression</code> is 'Level ''{0}'' must have a name expression (a ''column'' attribute or an <Expression> child'
     */
    public final _Def0 LevelMustHaveNameExpression = new _Def0("LevelMustHaveNameExpression", "Level ''{0}'' must have a name expression (a ''column'' attribute or an <Expression> child", null);

    /**
     * <code>PublicDimensionMustNotHaveForeignKey</code> is 'Dimension ''{0}'' has a foreign key. This attribute is only valid in private dimensions and dimension usages.'
     */
    public final _Def0 PublicDimensionMustNotHaveForeignKey = new _Def0("PublicDimensionMustNotHaveForeignKey", "Dimension ''{0}'' has a foreign key. This attribute is only valid in private dimensions and dimension usages.", null);

    /**
     * <code>HierarchyMustNotHaveMoreThanOneSource</code> is 'Hierarchy ''{0}'' has more than one source (memberReaderClass, <Table>, <Join> or <View>)'
     */
    public final _Def0 HierarchyMustNotHaveMoreThanOneSource = new _Def0("HierarchyMustNotHaveMoreThanOneSource", "Hierarchy ''{0}'' has more than one source (memberReaderClass, <Table>, <Join> or <View>)", null);

    /**
     * <code>DimensionUsageHasUnknownLevel</code> is 'In usage of dimension ''{0}'' in cube ''{1}'', the level ''{2}'' is unknown'
     */
    public final _Def4 DimensionUsageHasUnknownLevel = new _Def4("DimensionUsageHasUnknownLevel", "In usage of dimension ''{0}'' in cube ''{1}'', the level ''{2}'' is unknown", null);

    /**
     * <code>CalcMemberHasBadDimension</code> is 'Unknown dimension ''{0}'' for calculated member ''{1}'' in cube ''{2}'''
     */
    public final _Def4 CalcMemberHasBadDimension = new _Def4("CalcMemberHasBadDimension", "Unknown dimension ''{0}'' for calculated member ''{1}'' in cube ''{2}''", null);

    /**
     * <code>CalcMemberNotUnique</code> is 'Calculated member ''{0}'' already exists in cube ''{1}'''
     */
    public final _Def1 CalcMemberNotUnique = new _Def1("CalcMemberNotUnique", "Calculated member ''{0}'' already exists in cube ''{1}''", null);

    /**
     * <code>NeitherExprNorValueForCalcMemberProperty</code> is 'Member property must have a value or an expression. (Property ''{0}'' of member ''{1}'' of cube ''{2}''.)'
     */
    public final _Def4 NeitherExprNorValueForCalcMemberProperty = new _Def4("NeitherExprNorValueForCalcMemberProperty", "Member property must have a value or an expression. (Property ''{0}'' of member ''{1}'' of cube ''{2}''.)", null);

    /**
     * <code>ExprAndValueForMemberProperty</code> is 'Member property must not have both a value and an expression. (Property ''{0}'' of member ''{1}'' of cube ''{2}''.)'
     */
    public final _Def4 ExprAndValueForMemberProperty = new _Def4("ExprAndValueForMemberProperty", "Member property must not have both a value and an expression. (Property ''{0}'' of member ''{1}'' of cube ''{2}''.)", null);

    /**
     * <code>MemberFormatterLoadFailed</code> is 'Failed to load formatter class ''{0}'' for level ''{1}''.'
     */
    public final _Def1 MemberFormatterLoadFailed = new _Def1("MemberFormatterLoadFailed", "Failed to load formatter class ''{0}'' for level ''{1}''.", null);

    /**
     * <code>CellFormatterLoadFailed</code> is 'Failed to load formatter class ''{0}'' for member ''{1}''.'
     */
    public final _Def1 CellFormatterLoadFailed = new _Def1("CellFormatterLoadFailed", "Failed to load formatter class ''{0}'' for member ''{1}''.", null);

    /**
     * <code>HierarchyMustHaveForeignKey</code> is 'Hierarchy ''{0}'' in cube ''{1}'' must have a foreign key, since it is not based on the cube's fact table.'
     */
    public final _Def1 HierarchyMustHaveForeignKey = new _Def1("HierarchyMustHaveForeignKey", "Hierarchy ''{0}'' in cube ''{1}'' must have a foreign key, since it is not based on the cube's fact table.", null);

    /**
     * <code>HierarchyInvalidForeignKey</code> is 'Foreign key ''{0}'' of hierarchy ''{1}'' in cube ''{2}'' is not a column in the fact table.'
     */
    public final _Def4 HierarchyInvalidForeignKey = new _Def4("HierarchyInvalidForeignKey", "Foreign key ''{0}'' of hierarchy ''{1}'' in cube ''{2}'' is not a column in the fact table.", null);

    /**
     * <code>UdfClassNotFound</code> is 'Failed to load user-defined function ''{0}'': class ''{1}'' not found'
     */
    public final _Def1 UdfClassNotFound = new _Def1("UdfClassNotFound", "Failed to load user-defined function ''{0}'': class ''{1}'' not found", null);

    /**
     * <code>UdfClassWrongIface</code> is 'Failed to load user-defined function ''{0}'': class ''{1}'' does not implement the required interface ''{2}'''
     */
    public final _Def4 UdfClassWrongIface = new _Def4("UdfClassWrongIface", "Failed to load user-defined function ''{0}'': class ''{1}'' does not implement the required interface ''{2}''", null);

    /**
     * <code>UdfDuplicateName</code> is 'Duplicate user-defined function ''{0}'''
     */
    public final _Def0 UdfDuplicateName = new _Def0("UdfDuplicateName", "Duplicate user-defined function ''{0}''", null);

    /**
     * <code>NamedSetNotUnique</code> is 'Named set ''{0}'' already exists in cube ''{1}'''
     */
    public final _Def1 NamedSetNotUnique = new _Def1("NamedSetNotUnique", "Named set ''{0}'' already exists in cube ''{1}''", null);

    /**
     * <code>UnknownNamedSetHasBadFormula</code> is 'Named set in cube ''{0}'' has bad formula'
     */
    public final _Def0 UnknownNamedSetHasBadFormula = new _Def0("UnknownNamedSetHasBadFormula", "Named set in cube ''{0}'' has bad formula", null);

    /**
     * <code>NamedSetHasBadFormula</code> is 'Named set ''{0}'' has bad formula'
     */
    public final _Def0 NamedSetHasBadFormula = new _Def0("NamedSetHasBadFormula", "Named set ''{0}'' has bad formula", null);

    /**
     * <code>MeasureOrdinalsNotUnique</code> is 'Cube ''{0}'': Ordinal {1} is not unique: ''{2}'' and ''{3}'''
     */
    public final _Def7 MeasureOrdinalsNotUnique = new _Def7("MeasureOrdinalsNotUnique", "Cube ''{0}'': Ordinal {1} is not unique: ''{2}'' and ''{3}''", null);

    /**
     * <code>BadMeasureSource</code> is 'Cube ''{0}'': Measure ''{1}'' must contain either a source column or a source expression, but not both'
     */
    public final _Def1 BadMeasureSource = new _Def1("BadMeasureSource", "Cube ''{0}'': Measure ''{1}'' must contain either a source column or a source expression, but not both", null);

    /**
     * <code>DuplicateSchemaParameter</code> is 'Duplicate parameter ''{0}'' in schema'
     */
    public final _Def0 DuplicateSchemaParameter = new _Def0("DuplicateSchemaParameter", "Duplicate parameter ''{0}'' in schema", null);

    /**
     * <code>UnknownAggregator</code> is 'Unknown aggregator ''{0}''; valid aggregators are: {1}'
     */
    public final _Def1 UnknownAggregator = new _Def1("UnknownAggregator", "Unknown aggregator ''{0}''; valid aggregators are: {1}", null);

    /**
     * <code>RoleUnionGrants</code> is 'Union role must not contain grants'
     */
    public final _Def3 RoleUnionGrants = new _Def3("RoleUnionGrants", "Union role must not contain grants", null);

    /**
     * <code>UnknownRole</code> is 'Unknown role ''{0}'''
     */
    public final _Def0 UnknownRole = new _Def0("UnknownRole", "Unknown role ''{0}''", null);

    /**
     * <code>CreateTableFailed</code> is 'Mondrian loader could not create table ''{0}''.'
     */
    public final _Def0 CreateTableFailed = new _Def0("CreateTableFailed", "Mondrian loader could not create table ''{0}''.", null);

    /**
     * <code>CreateIndexFailed</code> is 'Mondrian loader could not create index ''{0}'' on table ''{1}''.'
     */
    public final _Def1 CreateIndexFailed = new _Def1("CreateIndexFailed", "Mondrian loader could not create index ''{0}'' on table ''{1}''.", null);

    /**
     * <code>MissingArg</code> is 'Argument ''{0}'' must be specified.'
     */
    public final _Def0 MissingArg = new _Def0("MissingArg", "Argument ''{0}'' must be specified.", null);

    /**
     * <code>InvalidInsertLine</code> is 'Input line is not a valid INSERT statement; line {0,number}: {1}.'
     */
    public final _Def6 InvalidInsertLine = new _Def6("InvalidInsertLine", "Input line is not a valid INSERT statement; line {0,number}: {1}.", null);

    /**
     * <code>LimitExceededDuringCrossjoin</code> is 'Size of CrossJoin result ({0,number}) exceeded limit ({1,number})'
     */
    public final _Def10 LimitExceededDuringCrossjoin = new _Def10("LimitExceededDuringCrossjoin", "Size of CrossJoin result ({0,number}) exceeded limit ({1,number})", null);

    /**
     * <code>TotalMembersLimitExceeded</code> is 'Total number of Members in result ({0,number}) exceeded limit ({1,number})'
     */
    public final _Def10 TotalMembersLimitExceeded = new _Def10("TotalMembersLimitExceeded", "Total number of Members in result ({0,number}) exceeded limit ({1,number})", null);

    /**
     * <code>MemberFetchLimitExceeded</code> is 'Number of members to be read exceeded limit ({0,number})'
     */
    public final _Def11 MemberFetchLimitExceeded = new _Def11("MemberFetchLimitExceeded", "Number of members to be read exceeded limit ({0,number})", null);

    /**
     * <code>QueryCanceled</code> is 'Query canceled'
     */
    public final _Def12 QueryCanceled = new _Def12("QueryCanceled", "Query canceled", null);

    /**
     * <code>QueryTimeout</code> is 'Query timeout of {0,number} seconds reached'
     */
    public final _Def13 QueryTimeout = new _Def13("QueryTimeout", "Query timeout of {0,number} seconds reached", null);

    /**
     * <code>IterationLimitExceeded</code> is 'Number of iterations exceeded limit of {0,number}'
     */
    public final _Def11 IterationLimitExceeded = new _Def11("IterationLimitExceeded", "Number of iterations exceeded limit of {0,number}", null);

    /**
     * <code>InvalidHierarchyCondition</code> is 'Hierarchy ''{0}'' is invalid (has no members)'
     */
    public final _Def14 InvalidHierarchyCondition = new _Def14("InvalidHierarchyCondition", "Hierarchy ''{0}'' is invalid (has no members)", null);

    /**
     * <code>TooManyMessageRecorderErrors</code> is 'Context ''{0}'': Exceeded number of allowed errors ''{1,number}'''
     */
    public final _Def15 TooManyMessageRecorderErrors = new _Def15("TooManyMessageRecorderErrors", "Context ''{0}'': Exceeded number of allowed errors ''{1,number}''", null);

    /**
     * <code>ForceMessageRecorderError</code> is 'Context ''{0}'': Client forcing return with errors ''{1,number}'''
     */
    public final _Def15 ForceMessageRecorderError = new _Def15("ForceMessageRecorderError", "Context ''{0}'': Client forcing return with errors ''{1,number}''", null);

    /**
     * <code>UnknownLevelName</code> is 'Context ''{0}'': The Hierarchy Level ''{1}'' does not have a Level named ''{2}'''
     */
    public final _Def16 UnknownLevelName = new _Def16("UnknownLevelName", "Context ''{0}'': The Hierarchy Level ''{1}'' does not have a Level named ''{2}''", null);

    /**
     * <code>DuplicateLevelNames</code> is 'Context ''{0}'': Two levels share the same name ''{1}'''
     */
    public final _Def17 DuplicateLevelNames = new _Def17("DuplicateLevelNames", "Context ''{0}'': Two levels share the same name ''{1}''", null);

    /**
     * <code>DuplicateLevelColumnNames</code> is 'Context ''{0}'': Two levels, ''{1}'' and ''{2}'',  share the same foreign column name ''{3}'''
     */
    public final _Def18 DuplicateLevelColumnNames = new _Def18("DuplicateLevelColumnNames", "Context ''{0}'': Two levels, ''{1}'' and ''{2}'',  share the same foreign column name ''{3}''", null);

    /**
     * <code>DuplicateMeasureColumnNames</code> is 'Context ''{0}'': Two measures, ''{1}'' and ''{2}'',  share the same column name ''{3}'''
     */
    public final _Def18 DuplicateMeasureColumnNames = new _Def18("DuplicateMeasureColumnNames", "Context ''{0}'': Two measures, ''{1}'' and ''{2}'',  share the same column name ''{3}''", null);

    /**
     * <code>DuplicateLevelMeasureColumnNames</code> is 'Context ''{0}'': The level ''{1}'' and the measuer ''{2}'',  share the same column name ''{3}'''
     */
    public final _Def18 DuplicateLevelMeasureColumnNames = new _Def18("DuplicateLevelMeasureColumnNames", "Context ''{0}'': The level ''{1}'' and the measuer ''{2}'',  share the same column name ''{3}''", null);

    /**
     * <code>DuplicateMeasureNames</code> is 'Context ''{0}'': Two measures share the same name ''{1}'''
     */
    public final _Def17 DuplicateMeasureNames = new _Def17("DuplicateMeasureNames", "Context ''{0}'': Two measures share the same name ''{1}''", null);

    /**
     * <code>DuplicateFactForeignKey</code> is 'Context ''{0}'': Duplicate fact foreign keys ''{1}'' for key ''{2}''.'
     */
    public final _Def16 DuplicateFactForeignKey = new _Def16("DuplicateFactForeignKey", "Context ''{0}'': Duplicate fact foreign keys ''{1}'' for key ''{2}''.", null);

    /**
     * <code>UnknownLeftJoinCondition</code> is 'Context ''{0}'': Failed to find left join condition in fact table ''{1}'' for foreign key ''{2}''.'
     */
    public final _Def16 UnknownLeftJoinCondition = new _Def16("UnknownLeftJoinCondition", "Context ''{0}'': Failed to find left join condition in fact table ''{1}'' for foreign key ''{2}''.", null);

    /**
     * <code>UnknownHierarchyName</code> is 'Context ''{0}'': The Hierarchy ''{1}'' does not exist"'
     */
    public final _Def17 UnknownHierarchyName = new _Def17("UnknownHierarchyName", "Context ''{0}'': The Hierarchy ''{1}'' does not exist\"", null);

    /**
     * <code>BadLevelNameFormat</code> is 'Context ''{0}'': The Level name ''{1}'' should be [usage hierarchy name].[level name].'
     */
    public final _Def17 BadLevelNameFormat = new _Def17("BadLevelNameFormat", "Context ''{0}'': The Level name ''{1}'' should be [usage hierarchy name].[level name].", null);

    /**
     * <code>BadMeasureNameFormat</code> is 'Context ''{0}'': The Measures name ''{1}'' should be [Measures].[measure name].'
     */
    public final _Def17 BadMeasureNameFormat = new _Def17("BadMeasureNameFormat", "Context ''{0}'': The Measures name ''{1}'' should be [Measures].[measure name].", null);

    /**
     * <code>BadMeasures</code> is 'Context ''{0}'': This name ''{1}'' must be the string "Measures".'
     */
    public final _Def17 BadMeasures = new _Def17("BadMeasures", "Context ''{0}'': This name ''{1}'' must be the string \"Measures\".", null);

    /**
     * <code>UnknownMeasureName</code> is 'Context ''{0}'': Measures does not have a measure named ''{1}'''
     */
    public final _Def17 UnknownMeasureName = new _Def17("UnknownMeasureName", "Context ''{0}'': Measures does not have a measure named ''{1}''", null);

    /**
     * <code>NullAttributeString</code> is 'Context ''{0}'': The value for the attribute ''{1}'' is null.'
     */
    public final _Def17 NullAttributeString = new _Def17("NullAttributeString", "Context ''{0}'': The value for the attribute ''{1}'' is null.", null);

    /**
     * <code>EmptyAttributeString</code> is 'Context ''{0}'': The value for the attribute ''{1}'' is empty (length is zero).'
     */
    public final _Def17 EmptyAttributeString = new _Def17("EmptyAttributeString", "Context ''{0}'': The value for the attribute ''{1}'' is empty (length is zero).", null);

    /**
     * <code>MissingDefaultAggRule</code> is 'There is no default aggregate recognition rule with tag ''{0}''.'
     */
    public final _Def0 MissingDefaultAggRule = new _Def0("MissingDefaultAggRule", "There is no default aggregate recognition rule with tag ''{0}''.", null);

    /**
     * <code>AggRuleParse</code> is 'Error while parsing default aggregate recognition ''{0}''.'
     */
    public final _Def0 AggRuleParse = new _Def0("AggRuleParse", "Error while parsing default aggregate recognition ''{0}''.", null);

    /**
     * <code>BadMeasureName</code> is 'Context ''{0}'': Failed to find Measure name ''{1}'' for cube ''{2}''.'
     */
    public final _Def16 BadMeasureName = new _Def16("BadMeasureName", "Context ''{0}'': Failed to find Measure name ''{1}'' for cube ''{2}''.", null);

    /**
     * <code>BadRolapStarLeftJoinCondition</code> is 'Context ''{0}'': Bad RolapStar left join condition type: ''{1}'' ''{2}''.'
     */
    public final _Def16 BadRolapStarLeftJoinCondition = new _Def16("BadRolapStarLeftJoinCondition", "Context ''{0}'': Bad RolapStar left join condition type: ''{1}'' ''{2}''.", null);

    /**
     * <code>SqlQueryFailed</code> is 'Context ''{0}'': Sql query failed to run ''{1}''.'
     */
    public final _Def17 SqlQueryFailed = new _Def17("SqlQueryFailed", "Context ''{0}'': Sql query failed to run ''{1}''.", null);

    /**
     * <code>AggLoadingError</code> is 'Error while loading/reloading aggregates.'
     */
    public final _Def3 AggLoadingError = new _Def3("AggLoadingError", "Error while loading/reloading aggregates.", null);

    /**
     * <code>AggLoadingExceededErrorCount</code> is 'Too many errors, ''{0,number}'', while loading/reloadin aggregates.'
     */
    public final _Def8 AggLoadingExceededErrorCount = new _Def8("AggLoadingExceededErrorCount", "Too many errors, ''{0,number}'', while loading/reloadin aggregates.", null);

    /**
     * <code>UnknownFactTableColumn</code> is 'Context ''{0}'': For Fact table ''{1}'', the column ''{2}'' is neither a measure or foreign key".'
     */
    public final _Def16 UnknownFactTableColumn = new _Def16("UnknownFactTableColumn", "Context ''{0}'': For Fact table ''{1}'', the column ''{2}'' is neither a measure or foreign key\".", null);

    /**
     * <code>AggMultipleMatchingMeasure</code> is 'Context ''{0}'': Candidate aggregate table ''{1}'' for fact table ''{2}'' has ''{3,number}'' columns matching measure ''{4}'', ''{5}'', ''{6}''".'
     */
    public final _Def19 AggMultipleMatchingMeasure = new _Def19("AggMultipleMatchingMeasure", "Context ''{0}'': Candidate aggregate table ''{1}'' for fact table ''{2}'' has ''{3,number}'' columns matching measure ''{4}'', ''{5}'', ''{6}''\".", null);

    /**
     * <code>CouldNotLoadDefaultAggregateRules</code> is 'Could not load default aggregate rules ''{0}''.'
     */
    public final _Def2 CouldNotLoadDefaultAggregateRules = new _Def2("CouldNotLoadDefaultAggregateRules", "Could not load default aggregate rules ''{0}''.", null);

    /**
     * <code>FailedCreateNewDefaultAggregateRules</code> is 'Failed to create new default aggregate rules using property ''{0}'' with value ''{1}''.'
     */
    public final _Def17 FailedCreateNewDefaultAggregateRules = new _Def17("FailedCreateNewDefaultAggregateRules", "Failed to create new default aggregate rules using property ''{0}'' with value ''{1}''.", null);

    /**
     * <code>CubeRelationNotTable</code> is 'The Cube ''{0}'' relation is not a MondrianDef.Table but rather ''{1}''.'
     */
    public final _Def17 CubeRelationNotTable = new _Def17("CubeRelationNotTable", "The Cube ''{0}'' relation is not a MondrianDef.Table but rather ''{1}''.", null);

    /**
     * <code>AttemptToChangeTableUsage</code> is 'JdbcSchema.Table ''{0}'' already set to usage ''{1}'' and can not be reset to usage ''{2}''.'
     */
    public final _Def4 AttemptToChangeTableUsage = new _Def4("AttemptToChangeTableUsage", "JdbcSchema.Table ''{0}'' already set to usage ''{1}'' and can not be reset to usage ''{2}''.", null);

    /**
     * <code>BadJdbcFactoryClassName</code> is 'JdbcSchema Factory classname ''{0}'', class not found.'
     */
    public final _Def0 BadJdbcFactoryClassName = new _Def0("BadJdbcFactoryClassName", "JdbcSchema Factory classname ''{0}'', class not found.", null);

    /**
     * <code>BadJdbcFactoryInstantiation</code> is 'JdbcSchema Factory classname ''{0}'', can not instantiate.'
     */
    public final _Def0 BadJdbcFactoryInstantiation = new _Def0("BadJdbcFactoryInstantiation", "JdbcSchema Factory classname ''{0}'', can not instantiate.", null);

    /**
     * <code>BadJdbcFactoryAccess</code> is 'JdbcSchema Factory classname ''{0}'', illegal access.'
     */
    public final _Def0 BadJdbcFactoryAccess = new _Def0("BadJdbcFactoryAccess", "JdbcSchema Factory classname ''{0}'', illegal access.", null);

    /**
     * <code>NonNumericFactCountColumn</code> is 'Candidate aggregate table ''{0}'' for fact table ''{1}'' has candidate fact count column ''{2}'' has type ''{3}'' that is not numeric.'
     */
    public final _Def18 NonNumericFactCountColumn = new _Def18("NonNumericFactCountColumn", "Candidate aggregate table ''{0}'' for fact table ''{1}'' has candidate fact count column ''{2}'' has type ''{3}'' that is not numeric.", null);

    /**
     * <code>TooManyFactCountColumns</code> is 'Candidate aggregate table ''{0}'' for fact table ''{1}'' has ''{2,number}'' fact count columns.'
     */
    public final _Def20 TooManyFactCountColumns = new _Def20("TooManyFactCountColumns", "Candidate aggregate table ''{0}'' for fact table ''{1}'' has ''{2,number}'' fact count columns.", null);

    /**
     * <code>NoFactCountColumns</code> is 'Candidate aggregate table ''{0}'' for fact table ''{1}'' has no fact count columns.'
     */
    public final _Def17 NoFactCountColumns = new _Def17("NoFactCountColumns", "Candidate aggregate table ''{0}'' for fact table ''{1}'' has no fact count columns.", null);

    /**
     * <code>NoMeasureColumns</code> is 'Candidate aggregate table ''{0}'' for fact table ''{1}'' has no measure columns.'
     */
    public final _Def17 NoMeasureColumns = new _Def17("NoMeasureColumns", "Candidate aggregate table ''{0}'' for fact table ''{1}'' has no measure columns.", null);

    /**
     * <code>TooManyMatchingForeignKeyColumns</code> is 'Candidate aggregate table ''{0}'' for fact table ''{1}'' had ''{2,number}'' columns matching foreign key ''{3}'''
     */
    public final _Def21 TooManyMatchingForeignKeyColumns = new _Def21("TooManyMatchingForeignKeyColumns", "Candidate aggregate table ''{0}'' for fact table ''{1}'' had ''{2,number}'' columns matching foreign key ''{3}''", null);

    /**
     * <code>DoubleMatchForLevel</code> is 'Double Match for candidate aggregate table ''{0}'' for fact table ''{1}'' and column ''{2}'' matched two hierarchies: 1) table=''{3}'', column=''{4}'' and 2) table=''{5}'', column=''{6}'''
     */
    public final _Def22 DoubleMatchForLevel = new _Def22("DoubleMatchForLevel", "Double Match for candidate aggregate table ''{0}'' for fact table ''{1}'' and column ''{2}'' matched two hierarchies: 1) table=''{3}'', column=''{4}'' and 2) table=''{5}'', column=''{6}''", null);

    /**
     * <code>AggUnknownColumn</code> is 'Candidate aggregate table ''{0}'' for fact table ''{1}'' has a column ''{2}'' with unknown usage.'
     */
    public final _Def16 AggUnknownColumn = new _Def16("AggUnknownColumn", "Candidate aggregate table ''{0}'' for fact table ''{1}'' has a column ''{2}'' with unknown usage.", null);

    /**
     * <code>NoAggregatorFound</code> is 'No aggregator found while converting fact table aggregator: for usage
     * ''{0}'', fact aggregator ''{1}'' and sibling aggregator ''{2}'''
     */
    public final _Def16 NoAggregatorFound = new _Def16("NoAggregatorFound", "No aggregator found while converting fact table aggregator: for usage\n        ''{0}'', fact aggregator ''{1}'' and sibling aggregator ''{2}''", null);

    /**
     * <code>NoColumnNameFromExpression</code> is 'Could not get a column name from a level key expression: ''{0}''.'
     */
    public final _Def2 NoColumnNameFromExpression = new _Def2("NoColumnNameFromExpression", "Could not get a column name from a level key expression: ''{0}''.", null);

    /**
     * <code>AggTableZeroSize</code> is 'Zero size Aggregate table ''{0}'' for Fact Table ''{1}''.'
     */
    public final _Def17 AggTableZeroSize = new _Def17("AggTableZeroSize", "Zero size Aggregate table ''{0}'' for Fact Table ''{1}''.", null);

    /**
     * <code>CacheFlushRegionMustContainMembers</code> is 'Region of cells to be flushed must contain measures.'
     */
    public final _Def3 CacheFlushRegionMustContainMembers = new _Def3("CacheFlushRegionMustContainMembers", "Region of cells to be flushed must contain measures.", null);

    /**
     * <code>CacheFlushUnionDimensionalityMismatch</code> is 'Cannot union cell regions of different dimensionalities. (Dimensionalities are ''{0}'', ''{1}''.)'
     */
    public final _Def1 CacheFlushUnionDimensionalityMismatch = new _Def1("CacheFlushUnionDimensionalityMismatch", "Cannot union cell regions of different dimensionalities. (Dimensionalities are ''{0}'', ''{1}''.)", null);

    /**
     * <code>CacheFlushCrossjoinDimensionsInCommon</code> is 'Cannot crossjoin cell regions which have dimensions in common. (Dimensionalities are {0}.)'
     */
    public final _Def0 CacheFlushCrossjoinDimensionsInCommon = new _Def0("CacheFlushCrossjoinDimensionsInCommon", "Cannot crossjoin cell regions which have dimensions in common. (Dimensionalities are {0}.)", null);

    /**
     * <code>NativeEvaluationUnsupported</code> is 'Native evaluation not supported for this usage of function ''{0}'''
     */
    public final _Def23 NativeEvaluationUnsupported = new _Def23("NativeEvaluationUnsupported", "Native evaluation not supported for this usage of function ''{0}''", null);


    /**
     * Definition for resources which
     * return a {@link mondrian.olap.MondrianException} exception and
     * take arguments 'String p0'.
     */
    public final class _Def0 extends org.eigenbase.resgen.ResourceDefinition {
        _Def0(String key, String baseMessage, String[] props) {
            super(key, baseMessage, props);
        }
        public String str(String p0) {
            return instantiate(MondrianResource.this, new Object[] {p0}).toString();
        }
        public mondrian.olap.MondrianException ex(String p0) {
            return new mondrian.olap.MondrianException(instantiate(MondrianResource.this, new Object[] {p0}).toString());
        }
        public mondrian.olap.MondrianException ex(String p0, Throwable err) {
            return new mondrian.olap.MondrianException(instantiate(MondrianResource.this, new Object[] {p0}).toString(), err);
        }
    }

    /**
     * Definition for resources which
     * return a {@link mondrian.olap.MondrianException} exception and
     * take arguments 'String p0, String p1'.
     */
    public final class _Def1 extends org.eigenbase.resgen.ResourceDefinition {
        _Def1(String key, String baseMessage, String[] props) {
            super(key, baseMessage, props);
        }
        public String str(String p0, String p1) {
            return instantiate(MondrianResource.this, new Object[] {p0, p1}).toString();
        }
        public mondrian.olap.MondrianException ex(String p0, String p1) {
            return new mondrian.olap.MondrianException(instantiate(MondrianResource.this, new Object[] {p0, p1}).toString());
        }
        public mondrian.olap.MondrianException ex(String p0, String p1, Throwable err) {
            return new mondrian.olap.MondrianException(instantiate(MondrianResource.this, new Object[] {p0, p1}).toString(), err);
        }
    }

    /**
     * Definition for resources which
     * take arguments 'String p0'.
     */
    public final class _Def2 extends org.eigenbase.resgen.ResourceDefinition {
        _Def2(String key, String baseMessage, String[] props) {
            super(key, baseMessage, props);
        }
        public String str(String p0) {
            return instantiate(MondrianResource.this, new Object[] {p0}).toString();
        }
    }

    /**
     * Definition for resources which
     * return a {@link mondrian.olap.MondrianException} exception and
     * take arguments ''.
     */
    public final class _Def3 extends org.eigenbase.resgen.ResourceDefinition {
        _Def3(String key, String baseMessage, String[] props) {
            super(key, baseMessage, props);
        }
        public String str() {
            return instantiate(MondrianResource.this, emptyObjectArray).toString();
        }
        public mondrian.olap.MondrianException ex() {
            return new mondrian.olap.MondrianException(instantiate(MondrianResource.this, emptyObjectArray).toString());
        }
        public mondrian.olap.MondrianException ex(Throwable err) {
            return new mondrian.olap.MondrianException(instantiate(MondrianResource.this, emptyObjectArray).toString(), err);
        }
    }

    /**
     * Definition for resources which
     * return a {@link mondrian.olap.MondrianException} exception and
     * take arguments 'String p0, String p1, String p2'.
     */
    public final class _Def4 extends org.eigenbase.resgen.ResourceDefinition {
        _Def4(String key, String baseMessage, String[] props) {
            super(key, baseMessage, props);
        }
        public String str(String p0, String p1, String p2) {
            return instantiate(MondrianResource.this, new Object[] {p0, p1, p2}).toString();
        }
        public mondrian.olap.MondrianException ex(String p0, String p1, String p2) {
            return new mondrian.olap.MondrianException(instantiate(MondrianResource.this, new Object[] {p0, p1, p2}).toString());
        }
        public mondrian.olap.MondrianException ex(String p0, String p1, String p2, Throwable err) {
            return new mondrian.olap.MondrianException(instantiate(MondrianResource.this, new Object[] {p0, p1, p2}).toString(), err);
        }
    }

    /**
     * Definition for resources which
     * take arguments ''.
     */
    public final class _Def5 extends org.eigenbase.resgen.ResourceDefinition {
        _Def5(String key, String baseMessage, String[] props) {
            super(key, baseMessage, props);
        }
        public String str() {
            return instantiate(MondrianResource.this, emptyObjectArray).toString();
        }
    }

    /**
     * Definition for resources which
     * return a {@link mondrian.olap.MondrianException} exception and
     * take arguments 'Number p0, String p1'.
     */
    public final class _Def6 extends org.eigenbase.resgen.ResourceDefinition {
        _Def6(String key, String baseMessage, String[] props) {
            super(key, baseMessage, props);
        }
        public String str(Number p0, String p1) {
            return instantiate(MondrianResource.this, new Object[] {p0, p1}).toString();
        }
        public mondrian.olap.MondrianException ex(Number p0, String p1) {
            return new mondrian.olap.MondrianException(instantiate(MondrianResource.this, new Object[] {p0, p1}).toString());
        }
        public mondrian.olap.MondrianException ex(Number p0, String p1, Throwable err) {
            return new mondrian.olap.MondrianException(instantiate(MondrianResource.this, new Object[] {p0, p1}).toString(), err);
        }
    }

    /**
     * Definition for resources which
     * return a {@link mondrian.olap.MondrianException} exception and
     * take arguments 'String p0, String p1, String p2, String p3'.
     */
    public final class _Def7 extends org.eigenbase.resgen.ResourceDefinition {
        _Def7(String key, String baseMessage, String[] props) {
            super(key, baseMessage, props);
        }
        public String str(String p0, String p1, String p2, String p3) {
            return instantiate(MondrianResource.this, new Object[] {p0, p1, p2, p3}).toString();
        }
        public mondrian.olap.MondrianException ex(String p0, String p1, String p2, String p3) {
            return new mondrian.olap.MondrianException(instantiate(MondrianResource.this, new Object[] {p0, p1, p2, p3}).toString());
        }
        public mondrian.olap.MondrianException ex(String p0, String p1, String p2, String p3, Throwable err) {
            return new mondrian.olap.MondrianException(instantiate(MondrianResource.this, new Object[] {p0, p1, p2, p3}).toString(), err);
        }
    }

    /**
     * Definition for resources which
     * return a {@link mondrian.olap.MondrianException} exception and
     * take arguments 'Number p0'.
     */
    public final class _Def8 extends org.eigenbase.resgen.ResourceDefinition {
        _Def8(String key, String baseMessage, String[] props) {
            super(key, baseMessage, props);
        }
        public String str(Number p0) {
            return instantiate(MondrianResource.this, new Object[] {p0}).toString();
        }
        public mondrian.olap.MondrianException ex(Number p0) {
            return new mondrian.olap.MondrianException(instantiate(MondrianResource.this, new Object[] {p0}).toString());
        }
        public mondrian.olap.MondrianException ex(Number p0, Throwable err) {
            return new mondrian.olap.MondrianException(instantiate(MondrianResource.this, new Object[] {p0}).toString(), err);
        }
    }

    /**
     * Definition for resources which
     * return a {@link mondrian.olap.MondrianException} exception and
     * take arguments 'Number p0, Number p1'.
     */
    public final class _Def9 extends org.eigenbase.resgen.ResourceDefinition {
        _Def9(String key, String baseMessage, String[] props) {
            super(key, baseMessage, props);
        }
        public String str(Number p0, Number p1) {
            return instantiate(MondrianResource.this, new Object[] {p0, p1}).toString();
        }
        public mondrian.olap.MondrianException ex(Number p0, Number p1) {
            return new mondrian.olap.MondrianException(instantiate(MondrianResource.this, new Object[] {p0, p1}).toString());
        }
        public mondrian.olap.MondrianException ex(Number p0, Number p1, Throwable err) {
            return new mondrian.olap.MondrianException(instantiate(MondrianResource.this, new Object[] {p0, p1}).toString(), err);
        }
    }

    /**
     * Definition for resources which
     * return a {@link mondrian.olap.ResourceLimitExceededException} exception and
     * take arguments 'Number p0, Number p1'.
     */
    public final class _Def10 extends org.eigenbase.resgen.ResourceDefinition {
        _Def10(String key, String baseMessage, String[] props) {
            super(key, baseMessage, props);
        }
        public String str(Number p0, Number p1) {
            return instantiate(MondrianResource.this, new Object[] {p0, p1}).toString();
        }
        public mondrian.olap.ResourceLimitExceededException ex(Number p0, Number p1) {
            return new mondrian.olap.ResourceLimitExceededException(instantiate(MondrianResource.this, new Object[] {p0, p1}).toString());
        }
    }

    /**
     * Definition for resources which
     * return a {@link mondrian.olap.ResourceLimitExceededException} exception and
     * take arguments 'Number p0'.
     */
    public final class _Def11 extends org.eigenbase.resgen.ResourceDefinition {
        _Def11(String key, String baseMessage, String[] props) {
            super(key, baseMessage, props);
        }
        public String str(Number p0) {
            return instantiate(MondrianResource.this, new Object[] {p0}).toString();
        }
        public mondrian.olap.ResourceLimitExceededException ex(Number p0) {
            return new mondrian.olap.ResourceLimitExceededException(instantiate(MondrianResource.this, new Object[] {p0}).toString());
        }
    }

    /**
     * Definition for resources which
     * return a {@link mondrian.olap.QueryCanceledException} exception and
     * take arguments ''.
     */
    public final class _Def12 extends org.eigenbase.resgen.ResourceDefinition {
        _Def12(String key, String baseMessage, String[] props) {
            super(key, baseMessage, props);
        }
        public String str() {
            return instantiate(MondrianResource.this, emptyObjectArray).toString();
        }
        public mondrian.olap.QueryCanceledException ex() {
            return new mondrian.olap.QueryCanceledException(instantiate(MondrianResource.this, emptyObjectArray).toString());
        }
    }

    /**
     * Definition for resources which
     * return a {@link mondrian.olap.QueryTimeoutException} exception and
     * take arguments 'Number p0'.
     */
    public final class _Def13 extends org.eigenbase.resgen.ResourceDefinition {
        _Def13(String key, String baseMessage, String[] props) {
            super(key, baseMessage, props);
        }
        public String str(Number p0) {
            return instantiate(MondrianResource.this, new Object[] {p0}).toString();
        }
        public mondrian.olap.QueryTimeoutException ex(Number p0) {
            return new mondrian.olap.QueryTimeoutException(instantiate(MondrianResource.this, new Object[] {p0}).toString());
        }
    }

    /**
     * Definition for resources which
     * return a {@link mondrian.olap.InvalidHierarchyException} exception and
     * take arguments 'String p0'.
     */
    public final class _Def14 extends org.eigenbase.resgen.ResourceDefinition {
        _Def14(String key, String baseMessage, String[] props) {
            super(key, baseMessage, props);
        }
        public String str(String p0) {
            return instantiate(MondrianResource.this, new Object[] {p0}).toString();
        }
        public mondrian.olap.InvalidHierarchyException ex(String p0) {
            return new mondrian.olap.InvalidHierarchyException(instantiate(MondrianResource.this, new Object[] {p0}).toString());
        }
    }

    /**
     * Definition for resources which
     * take arguments 'String p0, Number p1'.
     */
    public final class _Def15 extends org.eigenbase.resgen.ResourceDefinition {
        _Def15(String key, String baseMessage, String[] props) {
            super(key, baseMessage, props);
        }
        public String str(String p0, Number p1) {
            return instantiate(MondrianResource.this, new Object[] {p0, p1}).toString();
        }
    }

    /**
     * Definition for resources which
     * take arguments 'String p0, String p1, String p2'.
     */
    public final class _Def16 extends org.eigenbase.resgen.ResourceDefinition {
        _Def16(String key, String baseMessage, String[] props) {
            super(key, baseMessage, props);
        }
        public String str(String p0, String p1, String p2) {
            return instantiate(MondrianResource.this, new Object[] {p0, p1, p2}).toString();
        }
    }

    /**
     * Definition for resources which
     * take arguments 'String p0, String p1'.
     */
    public final class _Def17 extends org.eigenbase.resgen.ResourceDefinition {
        _Def17(String key, String baseMessage, String[] props) {
            super(key, baseMessage, props);
        }
        public String str(String p0, String p1) {
            return instantiate(MondrianResource.this, new Object[] {p0, p1}).toString();
        }
    }

    /**
     * Definition for resources which
     * take arguments 'String p0, String p1, String p2, String p3'.
     */
    public final class _Def18 extends org.eigenbase.resgen.ResourceDefinition {
        _Def18(String key, String baseMessage, String[] props) {
            super(key, baseMessage, props);
        }
        public String str(String p0, String p1, String p2, String p3) {
            return instantiate(MondrianResource.this, new Object[] {p0, p1, p2, p3}).toString();
        }
    }

    /**
     * Definition for resources which
     * take arguments 'String p0, String p1, String p2, Number p3, String p4, String p5, String p6'.
     */
    public final class _Def19 extends org.eigenbase.resgen.ResourceDefinition {
        _Def19(String key, String baseMessage, String[] props) {
            super(key, baseMessage, props);
        }
        public String str(String p0, String p1, String p2, Number p3, String p4, String p5, String p6) {
            return instantiate(MondrianResource.this, new Object[] {p0, p1, p2, p3, p4, p5, p6}).toString();
        }
    }

    /**
     * Definition for resources which
     * take arguments 'String p0, String p1, Number p2'.
     */
    public final class _Def20 extends org.eigenbase.resgen.ResourceDefinition {
        _Def20(String key, String baseMessage, String[] props) {
            super(key, baseMessage, props);
        }
        public String str(String p0, String p1, Number p2) {
            return instantiate(MondrianResource.this, new Object[] {p0, p1, p2}).toString();
        }
    }

    /**
     * Definition for resources which
     * take arguments 'String p0, String p1, Number p2, String p3'.
     */
    public final class _Def21 extends org.eigenbase.resgen.ResourceDefinition {
        _Def21(String key, String baseMessage, String[] props) {
            super(key, baseMessage, props);
        }
        public String str(String p0, String p1, Number p2, String p3) {
            return instantiate(MondrianResource.this, new Object[] {p0, p1, p2, p3}).toString();
        }
    }

    /**
     * Definition for resources which
     * take arguments 'String p0, String p1, String p2, String p3, String p4, String p5, String p6'.
     */
    public final class _Def22 extends org.eigenbase.resgen.ResourceDefinition {
        _Def22(String key, String baseMessage, String[] props) {
            super(key, baseMessage, props);
        }
        public String str(String p0, String p1, String p2, String p3, String p4, String p5, String p6) {
            return instantiate(MondrianResource.this, new Object[] {p0, p1, p2, p3, p4, p5, p6}).toString();
        }
    }

    /**
     * Definition for resources which
     * return a {@link mondrian.olap.NativeEvaluationUnsupportedException} exception and
     * take arguments 'String p0'.
     */
    public final class _Def23 extends org.eigenbase.resgen.ResourceDefinition {
        _Def23(String key, String baseMessage, String[] props) {
            super(key, baseMessage, props);
        }
        public String str(String p0) {
            return instantiate(MondrianResource.this, new Object[] {p0}).toString();
        }
        public mondrian.olap.NativeEvaluationUnsupportedException ex(String p0) {
            return new mondrian.olap.NativeEvaluationUnsupportedException(instantiate(MondrianResource.this, new Object[] {p0}).toString());
        }
    }

}
