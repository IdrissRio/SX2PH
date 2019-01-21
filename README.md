# SX2PH: Hybrid Automata translator.

# What’s the problem the tool solves?
SX2PH is a tool that take as input an .xml and a .cfg file that describe a [SpaceEx](http://spaceex.imag.fr) model and generate a .pha file that describe the same model in [PHAVer](http://www-verimag.imag.fr/~frehse/phaver_web/)'s formalism.


# Dependencies
The only required library is [args4j](https://args4j.kohsuke.org) to parse the command line arguments.

## Command line arguments:

- **-iXML** : The path of the .XML input file. The path is relative to the JAR's location.
- **-iCFG** : The path of the .CFG input file. The path is relative to the JAR's location. (optional)
- **-o** : The path of the output file. If not specified the output stream is the stdout.
- **-v** : Verbose mode.

# Usage

```bash
java -jar SX2PH.jar -iXML some/path/spaceex-file.xml -iCFG some/path/spaceex-file.cfg -o /some/path/phaver-lite
```

## Example
Let consider the file bball.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<sspaceex xmlns="http://www-verimag.imag.fr/xml-namespaces/sspaceex" version="0.2" math="SpaceEx">
  <component id="ball_template">
    <param name="x" type="real" local="false" d1="1" d2="1" dynamics="any" />
    <param name="v" type="real" local="false" d1="1" d2="1" dynamics="any" />
    <param name="g" type="real" local="false" d1="1" d2="1" dynamics="const" />
    <param name="c" type="real" local="false" d1="1" d2="1" dynamics="const" />
    <param name="eps" type="real" local="false" d1="1" d2="1" dynamics="const" />
    <param name="hop" type="label" local="false" />
    <location id="1" name="state" x="174.5" y="225.5" width="135.0" height="73.0">
      <invariant>x &gt;= 0 &amp; x &lt;=10 &amp; v &lt;= 10 &amp; v&gt;=-10</invariant>
      <flow>x' == v &amp; v' == -g</flow>
    </location>
    <transition source="1" target="1">
      <label>hop</label>
      <guard>x &lt;= eps </guard>
	  <assignment>v :=  -v * 0.5 &amp; x :=x</assignment>
      <labelposition x="-41.0" y="-69.0" />
    </transition>
  </component>
  <component id="bouncing_ball">
    <param name="x" type="real" local="false" d1="1" d2="1" dynamics="any" controlled="true" />
    <param name="v" type="real" local="false" d1="1" d2="1" dynamics="any" controlled="true" />
    <param name="hop" type="label" local="false" />
    <bind component="ball_template" as="ball" x="238.0" y="106.0">
      <map key="x">x</map>
      <map key="v">v</map>
      <map key="g">9.8</map>
      <map key="c">0.75</map>
      <map key="eps">0</map>
      <map key="hop">hop</map>
    </bind>
  </component>
</sspaceex>
```

with the following configuration (bball.cfg file):

```
# analysis options
system = "bouncing_ball"
initially = " loc(state) & loc(x==2) & v==0"
scenario = "supp"
directions = "uni32"
sampling-time = 0.1
time-horizon = 40
iter-max = 5
output-variables = "x,v"
output-format = "GEN"
rel-err = 1.0e-12
abs-err = 1.0e-13
```

Running the command:
```bash
java -jar SX2PH -iXML bball.xml -o bball.pha
```
we obtain 

```phaver
// Created with SX2PH v. 0.1.1
// Instance of: bouncing_ball
// Number of transitions: 1
// Number of locations:1
// Number of instance: 1


// Automaton name: ball_template
// Instance name: ball
// Number of locations: 1
// Number of transitions: 1
// Number of parameter: 6
//*****************  Constants  *****************
ball_g:=9.8;
ball_c:=0.75;
ball_eps:=0.0;

//*****************  Automaton ball_template  *****************
automaton ball
    contr_var: ball_x ,ball_v;
    synclabs: hop;
    loc state: while ball_x >= 0 & ball_x <=10 & ball_v <= 10 & v>=-10  wait
    {
        ball_x' == ball_v  &
        ball_v' == -ball_g 
    };

    when ball_x <= ball_eps   sync hop do
    {
        ball_v ' == -ball_v * 0.5  &
        ball_x ' ==ball_x 
    } goto state;

    initially:true;
end
```



