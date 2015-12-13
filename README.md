## Hearty: Visualisation

_**Hearty**_ is a final project for the course _Solar Solution_ of _Interactive Media Arts_ at NYU Shanghai. It is a wearable heart rate monitor powered by a polycrystalline silicon solar panel. Data collected will be transmitted through the on-board Wifi chip of _Arduino Yun_ to the server and then be retrieved for further analysis.

This repository contains the code responsible for the visualisation of the data. It is builtin upon _Processing_ and written in _Kotlin_ along with the Java libraries _Processing_ provides. Below shows a demo sanpshot of the visualisation.

![A demo snapshot of the visualisation.](https://raw.githubusercontent.com/NbKevin/HeartRateSensorVisualisation/master/demo/demo.png)

#### Launching the program

This repository itself can be opened as a _IntelliJ IDEA_ project. Users of _IntelliJ IDEA_ can directly clone the repository, open it and launch the `visualisation.VisualisationKt`.

Alternatively, one can execute any class with the main entry method for `PApplet` as showed in the file `visualisation/Visualisation.kt::main` or as below.

```Kotlin
fun main(args: Array<String>) {
    PApplet.main(arrayOf("visualisation.HeartRateVisualisation"))
}
```

Due to the modification made for _Processing_ libraries to work on _Kotlin_, the code may no longer be binary compatible with _PDE_ or _Processing Developing Environment_. However, the basic usage and business logic remains intact and should not cause much trouble if transported back.

#### Licence and more

This project, as referred to by the name _**Hearty**_, releases its code part under the licence of Apache 2.0. You can find more information on the [documentation site](http://ima.nyu.sh/documentation) of _Interactive Media Arts_.

> Copyright 2015, Nb/Kevin<bn628@nyu.edu>
>
>  Licensed under the Apache License, Version 2.0 (the "License");
>  you may not use this file except in compliance with the License.
>  You may obtain a copy of the License at
>
>      http://www.apache.org/licenses/LICENSE-2.0
>
>  Unless required by applicable law or agreed to in writing, software
>  distributed under the License is distributed on an "AS IS" BASIS,
>  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
>  See the License for the specific language governing permissions and
>  limitations under the License.
