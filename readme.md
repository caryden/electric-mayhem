# Electric Mayhem: A Reactive Robot Behavior Control System for FTC

## Introduction

Electric Mayhem is a reactive, command-based robot behavior control system designed specifically for the FIRST Tech Challenge (FTC). The project aims to build upon the approach taken in FTCLib, incorporating Kotlin flows and coroutines to decouple various robot components like sensors and subsystems. This makes the overall system more reactive and easier to manage. The project features two main reactive BehaviorControllers: a FiniteStateMachine and a BehaviorTree.

**Note**: The project initially focused on a reactive version of a FiniteStateMachine but has since shifted its main focus to BehaviorTrees.

## Features

- **100% Kotlin**: The entire codebase is written in Kotlin, leveraging its modern syntax and features.
- **Optimized for FTC**: Designed with FIRST Tech Challenge teams in mind.
- **Modular Design**: Inspired by ROS and other modular approaches to robot software.

## Installation

To integrate Electric Mayhem into your project, click [![](https://jitpack.io/v/caryden/electric-mayhem.svg)](https://jitpack.io/#caryden/electric-mayhem) and follow the instructions to add it to your Gradle build files. 

## Usage

For usage examples, refer to the sample opModes in the `app` module. Additionally, unit tests provide further insights into the library's capabilities.

## Learning Resources

- For an introduction to Finite State Machines in the context of FTC, refer to [Game Manual Zero](https://gm0.org/en/stable/docs/software/state-machines.html).
- To learn more about BehaviorTrees, check out this [YouTube playlist](https://youtube.com/playlist?list=PLFQdM4LOGDr_vYJuo8YTRcmv3FrwczdKg&si=z4Xpl5PENQ6W9Unb).
- For an academic perspective on BehaviorTrees, read this [paper](https://arxiv.org/abs/1709.00084).
- Here is a [really good overview of kotlin flows](https://youtu.be/emk9_tVVLcc).

## Contributing

If you're interested in contributing, feel free to fork the repository and submit a pull request. All contributions are welcome!

## License

[MIT License](LICENSE)

## Acknowledgments

- [FTCLib](https://github.com/FTCLib/FTCLib) for the foundational approach to FTC robot behavior control.
- The Kotlin community for providing the language and tools that make this project possible.



