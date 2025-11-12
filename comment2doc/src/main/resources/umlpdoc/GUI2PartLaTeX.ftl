<#-- (c) https://github.com/MontiCore/monticore -->
<#-- GUIDocArtifact doc, PrintUtilForLaTex util -->
${tc.signature("doc", "util")}
<#assign gui = doc.getComponentAST()>
<#assign compname = gui.getComponentName()>
<#assign examples = doc.getExampleList()>
<#assign images = doc.getImageList()>
<#assign types = doc.getCDList()>
<#assign disTypes = doc.getCdTypesDisplay()>
<#assign relComponents = doc.getRelatedComponents()>
<#assign texpath = "src/tex/vis_lib/lst/">
<#assign imgpath = "src/tex/vis_lib/img/">

\documentclass[main]{subfiles}
\begin{document}

\lstdefinelanguage{JavaGUI}{
    morekeywords={page,component,adapts, import, package, before, after, replace, remove, @guard, @iterate, else, from, to},
    morekeywords=[2]{int, String, boolean, double, float, char, void},
    sensitive=true,
    morecomment=[l]{//},
    morecomment=[s]{/*}{*/},
    morestring=[b]"
}

\lstdefinestyle{customjavagui}{
    language=JavaGUI,
    basicstyle=\ttfamily\small,
    keywordstyle=\color{blue}\bfseries,
    keywordstyle=[2]\color{purple},
    commentstyle=\color{gray}\itshape,
    stringstyle=\color{teal},
    numberstyle=\tiny\color{gray},
    breaklines=true,
    frame=single,
    numbers=left,
    columns=fullflexible,
    breakatwhitespace=false,
    showstringspaces=false,
}

\lstdefinestyle{customjava}{
    language=Java,
    basicstyle=\ttfamily\small,
    keywordstyle=\color{blue}\bfseries,
    keywordstyle=[2]\color{purple},
    commentstyle=\color{gray}\itshape,
    stringstyle=\color{teal},
    numberstyle=\tiny\color{gray},
    breaklines=true,
    frame=single,
    numbers=left,
    columns=fullflexible,
    breakatwhitespace=false,
    showstringspaces=false,
}

\section{${compname}}\label{comp:${compname}}
<#assign componentComment = util.printComment(gui.getGUIComponentDeclaration().get_PreCommentList())>
<#if componentComment?has_content>
${componentComment}

</#if>
<#if examples?has_content>
    <#assign example = examples[0]>

\noindent Example in \ref{lst:${example}} demonstrates usage examples of the component.
<#if images?has_content>
Figure~\ref{fig:${example}} illustrates the component's UI.
</#if>

<#if doc.hasExampleModel(example)>
    \lstinputlisting[style=customjavagui, caption=${example}, label={lst:${example}}]{${example}.gui}
</#if>

    <#if images?has_content>
    <#assign image = images[0]>
    \begin{figure}[H]
    \centering
    \caption{GUI Visualization for \texttt{${example}}.}
    <#if image?ends_with("Small")>
    \includegraphics[width=0.4\textwidth]{${image}}
    <#else>
    \includegraphics[width=0.8\textwidth]{${image}}
    </#if>
    \label{fig:${example}}
    \end{figure}
    <#else>
    % Uncomment the following lines to add a visualization of the GUI component.
    % Replace $IMAGE$ with the image file path.
    %\begin{figure}[H]
    %\centering
    %\includegraphics[width=0.8\textwidth]{$IMAGE$}
    %\caption{GUI Visualization for \texttt{${example}}.}
    %\label{fig:${example}}
    %\end{figure}
    </#if>

</#if>

\subsection{API Parameters}

Table~\ref{tab:${compname}_params} lists the parameters that configure the \texttt{${compname}} component.

\vspace{-0.5em}
\begin{longtable}{>{\raggedright\arraybackslash}p{0.35\textwidth}
@{\hspace{-0.3em}}
>{\raggedright\arraybackslash}p{0.45\textwidth}
@{\hspace{-0.3em}}
>{\raggedleft\arraybackslash}p{0.2\textwidth}}
\caption{${compname} API Parameters} \label{tab:${compname}_params} \\
\toprule
\textbf{Parameter} & \textbf{Type} & \textbf{Default Value} \\
\midrule
\endfirsthead
\multicolumn{3}{c}{\tablename\ \thetable\ -- \textit{Continued from previous page}} \\
\toprule
\textbf{Parameter} & \textbf{Type} & \textbf{Default Value} \\
\midrule
\endhead
\midrule
\multicolumn{3}{r}{\textit{Continued on next page}} \\
\endfoot
\bottomrule
\endlastfoot
<#list gui.getGUIComponentDeclaration().getGUIParamList() as param>
    <#assign type = "">
    <#if param.isReferenceParam()>
        <#assign type = type + "?">
    </#if>
    <#assign type = type + param.getMCType().printType()?replace("_", "\\_")>
    <#assign description = util.printComment(param.get_PreCommentList())>
    <#if !param?is_first>
        \noalign{\vskip 0.3em}
    </#if>
    \texttt{\normalsize\textbf{${param.getName()}}} &
    \typename{\normalsize ${type}} &
    <#if param.isPresentDefault()>\texttt{\normalsize ${util.escapeForTexttt(util.print(param.getDefault()))}}<#else>---</#if> \\*
    \noalign{\vskip 0.3em}
    <#if description?has_content>
        \multicolumn{3}{@{}p{\textwidth}@{}}{%
        \hangindent=0.6em \hangafter=0 \normalsize\upshape\color{descriptiongray}${description}} \\
        \noalign{\vskip 0.3em}
    </#if>
    <#if param_has_next>
        \arrayrulecolor{lightgray}\midrule\arrayrulecolor{black}
    </#if>
</#list>
\end{longtable}

<#if types?has_content && relComponents?has_content>
\vspace{-1em}
\subsection{Related Types and Components}
This section outlines both the types and components related to the \texttt{${compname}} module.
<#list types as type>

    <#if disTypes?seq_contains(type)>
        \subsubsection{\texttt{${type}} Class Diagram (Listing~\protect\ref{fig:${type}_cd})}\label{cd:${type}}
        <#assign typeComment = util.printComment(doc.getCDAST(type).getCDDefinition().get_PreCommentList())>
        <#if typeComment?has_content>
            ${typeComment}

        </#if>
        \begin{lstlisting}[style=customjava,caption=${type},label={fig:${type}_cd}]
${doc.getCDModel(type)}
        \end{lstlisting}
        <#if doc.hasCDAST(type)>
            <#assign cdAST = doc.getCDAST(type)>
            <#if cdAST.getCDDefinition().getCDClassesList()?has_content>
                <#list cdAST.getCDDefinition().getCDClassesList() as clazz>
                    \paragraph{Class \texttt{${clazz.getName()}}}\mbox{}\\

                    <#assign classComment = util.printComment(clazz.get_PreCommentList())>
                    <#if classComment?has_content>
                        ${classComment}

                    </#if>

                <#-- Attributes Table -->
                    <#if util.hasAttributes(clazz)>
                        \subparagraph{Attributes}\mbox{}\\

                        Table~\ref{tab:${clazz.getName()}_attrs} lists the attributes defined for the \texttt{${clazz.getName()}} class.

                        \vspace{-0.5em}
                        \begin{longtable}{>{\raggedright\arraybackslash}p{0.35\textwidth}
                        @{\hspace{-0.3em}}
                        >{\raggedright\arraybackslash}p{0.45\textwidth}
                        @{\hspace{-0.3em}}
                        >{\raggedleft\arraybackslash}p{0.2\textwidth}}
                        \caption{Attributes of \texttt{${clazz.getName()}}} \label{tab:${clazz.getName()}_attrs} \\
                        \toprule
                        \textbf{Name} & \textbf{Type} & \textbf{Modifiers} \\
                        \midrule
                        \endfirsthead
                        \multicolumn{3}{c}{\tablename\ \thetable\ -- \textit{Continued from previous page}} \\
                        \toprule
                        \textbf{Name} & \textbf{Type} & \textbf{Modifiers} \\
                        \midrule
                        \endhead
                        \midrule
                        \multicolumn{3}{r}{\textit{Continued on next page}} \\
                        \endfoot
                        \bottomrule
                        \endlastfoot
                        <#list clazz.getCDAttributeList() as attr>
                            <#assign modifiers = util.getAttributeModifiers(attr)>
                            <#assign description = util.printComment(attr.get_PreCommentList())>
                            \noalign{\vskip 0.3em}
                            \texttt{\normalsize\textbf{${attr.getName()}}} &
                            \typename{\normalsize ${util.formatAttributeType(attr)}<#if attr.isPresentInitial()> = ${util.formatAttributeDefault(attr)}</#if>} &
                            <#if modifiers?has_content>\texttt{\normalsize ${modifiers}}<#else>---</#if> \\*
                            \noalign{\vskip 0.3em}
                            <#if description?has_content>
                                \multicolumn{3}{@{}p{\textwidth}@{}}{%
                                \hangindent=0.6em \hangafter=0 \normalsize\upshape\color{descriptiongray}${description}} \\
                                \noalign{\vskip 0.3em}
                            </#if>
                            <#if attr_has_next>
                                \arrayrulecolor{lightgray}\midrule\arrayrulecolor{black}
                            </#if>
                        </#list>
                        \end{longtable}

                    <#else>
                        \subparagraph{Attributes}\mbox{}\\

                        This class has no attributes.

                    </#if>

                    <#assign classAssociationMap = util.getClassAssociationMap(doc.getCDAST(type))>
                <#-- Associations Table -->
                    <#if util.hasAssociations(clazz.getName(), classAssociationMap)>
                        \subparagraph{Associations}\mbox{}\\

                        Table~\ref{tab:${clazz.getName()}_assocs} summarizes its associations with other classes.

                        \vspace{-0.5em}
                        \begin{longtable}{>{\raggedright\arraybackslash}p{0.3\textwidth}
                        >{\raggedright\arraybackslash}p{0.25\textwidth}
                        >{\raggedright\arraybackslash}p{0.25\textwidth}
                        >{\raggedleft\arraybackslash}p{0.3\textwidth}}
                        \caption{Associations of \texttt{${clazz.getName()}}} \label{tab:${clazz.getName()}_assocs} \\
                        \toprule
                        \textbf{Type} & \textbf{Target Class} & \textbf{Role} & \textbf{Cardinality} \\
                        \midrule
                        \endfirsthead
                        \multicolumn{4}{c}{\tablename\ \thetable\ -- \textit{Continued from previous page}} \\
                        \toprule
                        \textbf{Type} & \textbf{Target Class} & \textbf{Role} & \textbf{Cardinality} \\
                        \midrule
                        \endhead
                        \midrule
                        \multicolumn{4}{r}{\textit{Continued on next page}} \\
                        \endfoot
                        \bottomrule
                        \endlastfoot
                        <#list classAssociationMap[clazz.getName()] as assoc>
                            <#assign oppositeSide = util.getOppositeSide(assoc, clazz.getName())>
                            <#assign currentSide = util.getCurrentSide(assoc, clazz.getName())>
                            <#if oppositeSide??>
                                <#assign roleName = util.inferRole(oppositeSide)>
                                <#assign cardinality = util.getCardinalityString(currentSide)>
                                <#assign description = util.printComment(assoc.get_PreCommentList())>
                                \texttt{\normalsize\textbf{${util.getAssociationType(assoc)}}} &
                                \typename{\normalsize ${util.getTargetClassName(oppositeSide)}} &
                                <#if roleName?has_content>\texttt{\normalsize ${roleName}}<#else>---</#if> &
                                <#if cardinality?has_content>\texttt{\normalsize ${cardinality}}<#else>---</#if> \\*
                                \noalign{\vskip 0.3em}
                                <#if description?has_content>
                                    \multicolumn{4}{@{}p{\textwidth}@{}}{%
                                    \hangindent=0.6em \hangafter=0 \paramdesc{${description}}\hspace{0.6em}} \\
                                    \noalign{\vskip 0.3em}
                                </#if>
                                <#if assoc_has_next>
                                    \arrayrulecolor{lightgray}\midrule\arrayrulecolor{black}
                                </#if>
                            </#if>
                        </#list>
                        \end{longtable}

                    <#else>
                        \subparagraph{Associations}\mbox{}\\

                        This class has no associations.

                    </#if>

                </#list>
            </#if>
        </#if>
    <#else>
        \subsubsection{\texttt{${type}} Class Diagram}
        Further details on the class diagram can be found in \ref{cd:${type}}.
    </#if>
</#list>

\subsubsection{Related Components}
    Table~\ref{tab:${compname}_relComps} lists the components utilized by the \texttt{${compname}}, along with their associated references.

    \vspace{-0.5em}
    \begin{longtable}{
    >{\raggedright\arraybackslash}p{0.6\textwidth}
    >{\centering\arraybackslash}p{0.35\textwidth}
    }
    \caption{Components and Their References} \label{tab:${compname}_relComps} \\
    \toprule
    \textbf{Component Name} & \textbf{Reference} \\
    \midrule
    \endfirsthead

    \multicolumn{2}{c}{\tablename\ \thetable\ -- \textit{Continued from previous page}} \\
    \toprule
    \textbf{Component Name} & \textbf{Reference} \\
    \midrule
    \endhead

    \midrule
    \multicolumn{2}{r}{\textit{Continued on next page}} \\
    \endfoot

    \bottomrule
    \endlastfoot

    <#list relComponents as relComp>
        \texttt{${relComp}} & \ref{comp:${relComp}} \\
    </#list>

    \end{longtable}

<#else>
<#if types?has_content>
    \vspace{-1em}
    \subsection{Related Types}
    <#list types as type>

        <#if disTypes?seq_contains(type)>
            \subsubsection{\texttt{${type}} Class Diagram (Listing~\protect\ref{fig:${type}_cd})}\label{cd:${type}}
            <#assign typeComment = util.printComment(doc.getCDAST(type).getCDDefinition().get_PreCommentList())>
            <#if typeComment?has_content>
                ${typeComment}

            </#if>
            \begin{lstlisting}[style=customjava,caption=${type},label={fig:${type}_cd}]
            ${doc.getCDModel(type)}
            \end{lstlisting}
            <#if doc.hasCDAST(type)>
                <#assign cdAST = doc.getCDAST(type)>
                <#if cdAST.getCDDefinition().getCDClassesList()?has_content>
                    <#list cdAST.getCDDefinition().getCDClassesList() as clazz>
                        \paragraph{Class \texttt{${clazz.getName()}}}\mbox{}\\

                        <#assign classComment = util.printComment(clazz.get_PreCommentList())>
                        <#if classComment?has_content>
                            ${classComment}

                        </#if>

                    <#-- Attributes Table -->
                        <#if util.hasAttributes(clazz)>
                            \subparagraph{Attributes}\mbox{}\\

                            Table~\ref{tab:${clazz.getName()}_attrs} lists the attributes defined for the \texttt{${clazz.getName()}} class.

                            \vspace{-0.5em}
                            \begin{longtable}{>{\raggedright\arraybackslash}p{0.35\textwidth}
                            @{\hspace{-0.3em}}
                            >{\raggedright\arraybackslash}p{0.45\textwidth}
                            @{\hspace{-0.3em}}
                            >{\raggedleft\arraybackslash}p{0.2\textwidth}}
                            \caption{Attributes of \texttt{${clazz.getName()}}} \label{tab:${clazz.getName()}_attrs} \\
                            \toprule
                            \textbf{Name} & \textbf{Type} & \textbf{Modifiers} \\
                            \midrule
                            \endfirsthead
                            \multicolumn{3}{c}{\tablename\ \thetable\ -- \textit{Continued from previous page}} \\
                            \toprule
                            \textbf{Name} & \textbf{Type} & \textbf{Modifiers} \\
                            \midrule
                            \endhead
                            \midrule
                            \multicolumn{3}{r}{\textit{Continued on next page}} \\
                            \endfoot
                            \bottomrule
                            \endlastfoot
                            <#list clazz.getCDAttributeList() as attr>
                                <#assign modifiers = util.getAttributeModifiers(attr)>
                                <#assign description = util.printComment(attr.get_PreCommentList())>
                                \noalign{\vskip 0.3em}
                                \texttt{\normalsize\textbf{${attr.getName()}}} &
                                \typename{\normalsize ${util.formatAttributeType(attr)}<#if attr.isPresentInitial()> = ${util.formatAttributeDefault(attr)}</#if>} &
                                <#if modifiers?has_content>\texttt{\normalsize ${modifiers}}<#else>---</#if> \\*
                                \noalign{\vskip 0.3em}
                                <#if description?has_content>
                                    \multicolumn{3}{@{}p{\textwidth}@{}}{%
                                    \hangindent=0.6em \hangafter=0 \normalsize\upshape\color{descriptiongray}${description}} \\
                                    \noalign{\vskip 0.3em}
                                </#if>
                                <#if attr_has_next>
                                    \arrayrulecolor{lightgray}\midrule\arrayrulecolor{black}
                                </#if>
                            </#list>
                            \end{longtable}

                        <#else>
                            \subparagraph{Attributes}\mbox{}\\

                            This class has no attributes.

                        </#if>

                        <#assign classAssociationMap = util.getClassAssociationMap(doc.getCDAST(type))>
                    <#-- Associations Table -->
                        <#if util.hasAssociations(clazz.getName(), classAssociationMap)>
                            \subparagraph{Associations}\mbox{}\\

                            Table~\ref{tab:${clazz.getName()}_assocs} summarizes its associations with other classes.

                            \vspace{-0.5em}
                            \begin{longtable}{>{\raggedright\arraybackslash}p{0.3\textwidth}
                            >{\raggedright\arraybackslash}p{0.25\textwidth}
                            >{\raggedright\arraybackslash}p{0.25\textwidth}
                            >{\raggedleft\arraybackslash}p{0.3\textwidth}}
                            \caption{Associations of \texttt{${clazz.getName()}}} \label{tab:${clazz.getName()}_assocs} \\
                            \toprule
                            \textbf{Type} & \textbf{Target Class} & \textbf{Role} & \textbf{Cardinality} \\
                            \midrule
                            \endfirsthead
                            \multicolumn{4}{c}{\tablename\ \thetable\ -- \textit{Continued from previous page}} \\
                            \toprule
                            \textbf{Type} & \textbf{Target Class} & \textbf{Role} & \textbf{Cardinality} \\
                            \midrule
                            \endhead
                            \midrule
                            \multicolumn{4}{r}{\textit{Continued on next page}} \\
                            \endfoot
                            \bottomrule
                            \endlastfoot
                            <#list classAssociationMap[clazz.getName()] as assoc>
                                <#assign oppositeSide = util.getOppositeSide(assoc, clazz.getName())>
                                <#assign currentSide = util.getCurrentSide(assoc, clazz.getName())>
                                <#if oppositeSide??>
                                    <#assign roleName = util.inferRole(oppositeSide)>
                                    <#assign cardinality = util.getCardinalityString(currentSide)>
                                    <#assign description = util.printComment(assoc.get_PreCommentList())>
                                    \texttt{\normalsize\textbf{${util.getAssociationType(assoc)}}} &
                                    \typename{\normalsize ${util.getTargetClassName(oppositeSide)}} &
                                    <#if roleName?has_content>\texttt{\normalsize ${roleName}}<#else>---</#if> &
                                    <#if cardinality?has_content>\texttt{\normalsize ${cardinality}}<#else>---</#if> \\*
                                    \noalign{\vskip 0.3em}
                                    <#if description?has_content>
                                        \multicolumn{4}{@{}p{\textwidth}@{}}{%
                                        \hangindent=0.6em \hangafter=0 \paramdesc{${description}}\hspace{0.6em}} \\
                                        \noalign{\vskip 0.3em}
                                    </#if>
                                    <#if assoc_has_next>
                                        \arrayrulecolor{lightgray}\midrule\arrayrulecolor{black}
                                    </#if>
                                </#if>
                            </#list>
                            \end{longtable}

                        <#else>
                            \subparagraph{Associations}\mbox{}\\

                            This class has no associations.

                        </#if>

                    </#list>
                </#if>
            </#if>
        <#else>
            \subsubsection{\texttt{${type}} Class Diagram}
            Further details on the class diagram \texttt{${type}} can be found in \ref{cd:${type}}.
        </#if>
    </#list>
</#if>
<#if relComponents?has_content>
    \vspace{-1em}
    \subsection{Related Components}

    Table~\ref{tab:${compname}_relComps} lists the components utilized by the \texttt{${compname}}, along with their associated references.

    \vspace{-0.5em}
    \begin{longtable}{
    >{\raggedright\arraybackslash}p{0.6\textwidth}
    >{\centering\arraybackslash}p{0.35\textwidth}
    }
    \caption{Components and Their References} \label{tab:${compname}_relComps} \\
    \toprule
    \textbf{Component Name} & \textbf{Reference} \\
    \midrule
    \endfirsthead

    \multicolumn{2}{c}{\tablename\ \thetable\ -- \textit{Continued from previous page}} \\
    \toprule
    \textbf{Component Name} & \textbf{Reference} \\
    \midrule
    \endhead

    \midrule
    \multicolumn{2}{r}{\textit{Continued on next page}} \\
    \endfoot

    \bottomrule
    \endlastfoot

    <#list relComponents as relComp>
        \texttt{${relComp}} & \ref{comp:${relComp}} \\
    </#list>

    \end{longtable}
</#if>
</#if>

\end{document}