# Java + WebAssembly
This repository acts as a template to combine Java code with WebAssembly. The main purpose is to be able to write performant and efficient
code that can be run in the web. This will be used for multiple projects regarding my blog.

The idea is to be able to call Java functions that have been compiled to WebAssembly from JavaScript code. This allows for a clean cut
of frontend logic in JavaScript and efficient computation in Java. 

## Start Frontend
The frontend requires [Node.js](https://nodejs.org/en) to be installed. The pre-installed Vite frontend
uses React combined with Typescript for easy development. For larger projects Angular
should probably be installed through node.

The following node commands start the development server:

1. Install dependencies:\
`~/JavaWebAssembly/webapp: npm install`
2. Run development server:\
`~/JavaWebAssembly/webapp: npm run dev`

When accessing the hosted webpage, the messages _"Hello from Main!"_ and
_"Hello from Test!"_ in the console indicate successful WebAssembly integration.
The Web page should display _"Hello from React!"_ to indicate proper React integration.

## Java Compilation
To compile Java code to the intermediate WebAssembly format I am using [CheerPJ](https://cheerpj.com/).
With this resource, any Java _.jar_ file can be used with JavaScript. This allows for
independent development of the Java source code and the JavaScript frontend.
The dependency is accessed using the
`<script src="https://cjrtnc.leaningtech.com/3.0/cj3loader.js"></script>`
tag in the _index.html_ file, so no installations are needed.

**IMPORTANT:** [CheerPJ](https://cheerpj.com/) is only compatible with .jar files compiled
with Java 8.

### IntelliJ .jar compilation
When the IDE IntelliJ is used to open the project, the _.jar_ file can be built with configurations.
As the _.idea_ folder is present in the repository, a preconfigured
build configuration for building artifacts that create a
new _.jar_ file in the _webapp/lib_ folder is included:

1. Go to _Build -> Build Artifacts -> Rebuild_ in IntelliJ settings.

### Terminal .jar compilation
The _.jar_ file can also be easily created from the terminal using the following commands:
1. Compile java class files with Java 8:\
`~/JavaWebAssembly: javac -source 1.8 -target 1.8 -d out src/code/*.java`
2. Compile java class files to .jar file:\
`~/JavaWebAssembly: jar cfm webapp/lib/JavaWebAssembly.jar src/META-INF/MANIFEST.MF -C out .`
