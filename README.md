a tool to convert Kotlin Synthetic References to Android View elements to Viewbinding References

currently will convert Fragments. Activities (and hopefully viewholders) in progress

to Run:

will probably need to generate antlr boilerplate. The gradle task for this is `generateGrammarSource` so I believe `./gradlew generateGrammarSource` should do it.
After that you should be able to run `./gradlew <project_root>` on your android project root and the program will go through and convert any file with `Fragment`, `Activity` etc in the name.
