# Java ToString Format
A JetBrains IntelliJ platform plugin to format Java's default `toString()` output to a JSON-like structure.

## Why
When retrieving MBean data from JConsole or VisualVM the output is being formatted using Java's `toString()` method.
Though MBeans are very convenient large output becomes unreadable. Hence, I needed something to format this output.

__Why JSON?__  
The output already looks a lot like JSON, so I figured it would be fairly easy to use JSON as target format.

## Installation
1. Grab the build from [the Github releases](https://github.com/Juraji/intellij-tostring-format-plugin/releases) or build it yourself.
2. Install the plugin from IntelliJ's Plugins manager using the Cogwheel in the top right and selecting "Install plugin from disk...".
3. Open and editor, optionally select the `toString()` output, open up the actions pane and select "Format Java ToString to Json". Tada, your selection should be readable™.

## Known bugs
* __It is not very resilient...__  
  True, I created this for convenience, but it is definitely not monkey-proof.
* __Structural characters in values break the output.__  
  This is a linear lexical formatter. Meaning it does not (really) know in what context it is currently parsing.
  A `{` with in a string gets parsed as an object start and that's it.
