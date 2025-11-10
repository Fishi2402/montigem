${tc.signature("docs")}

\documentclass{article}
\usepackage{float}
\usepackage{subfiles}
\usepackage{booktabs}
\usepackage{array}
\usepackage{listings}
\usepackage{graphicx}
\usepackage{url}
\usepackage{amsmath}
\usepackage{hyperref}
\usepackage{enumitem}
\setlist[itemize]{itemsep=0.2em, topsep=0.2em, parsep=0pt, partopsep=0pt}
\usepackage{longtable}
\usepackage{xcolor}
\usepackage{amssymb}
\usepackage{tcolorbox}
\usepackage{seqsplit}
\usepackage{colortbl}
\usepackage{tabularx}
\usepackage{geometry}
\geometry{
a4paper,
left=25mm,
top=25mm,
right=25mm,
bottom=25mm
}

\definecolor{descriptiongray}{RGB}{60, 60, 60}
\definecolor{lightgray}{RGB}{200, 200, 200}

\newcommand{\paramdesc}[1]{%
\begin{minipage}{\textwidth}
\normalsize\upshape\color{descriptiongray}#1
\end{minipage}
}

% Command to handle long text
\newcommand{\typename}[1]{%
\texttt{\seqsplit{#1}}%
}

\begin{document}

<#list docs as doc>
    \subfile{${doc.getComponentAST().getComponentName()}}
</#list>

\end{document}