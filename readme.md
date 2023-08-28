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

## Core Elements of Behavior Tree Implementation

### BehaviorTree Class

The `BehaviorTree` class serves as the main controller for the behavior tree. It takes a `CoroutineDispatcher` and a root node as parameters and manages the execution of the tree.

### Node

The `Node` class is the basic building block of the behavior tree. It defines the behavior that the tree should execute.

### ConditionNode

The `ConditionNode` class represents conditions in the behavior tree. It extends the `Node` class and adds additional functionality specific to conditions.

### ActionNode

The 'ActionNode' class represents an actiont that will be taken (a leaf node in the behavior tree).  This will typically amount to issuing a Command to a particular subsystem to change its target control state.  e.g. turret.MoveToPosition(120.0) 

### ControlNode

The `ControlNode` class is a type of node that can have child nodes. It is used to build complex behaviors by combining simpler nodes.

#### Specific Control Nodes in Electric Mayhem

##### FallbackNode
The `FallbackNode` class executes its children from left to right and returns a status code of `SUCCESS` if any of its children return `SUCCESS`. If none of the children return `SUCCESS`, it returns `FAILURE`.

##### ParallelNode
The `ParallelNode` class executes all of its children in parallel. It returns `SUCCESS` if all children return `SUCCESS`, `CANCELLED` if any child is cancelled, and `FAILURE` otherwise.

##### SequenceNode
The `SequenceNode` class executes its children from left to right and returns immediately with a status code of `FAILURE` if any of its children return `FAILURE`. If all children return `SUCCESS`, it returns `SUCCESS`.


### DecoratorNode

The `DecoratorNode` class is used to modify the behavior of its child node. It wraps around another node and changes its behavior based on certain conditions.

### NodeStatus

The `NodeStatus` enum defines the possible statuses a node can have. The statuses are Idle, Success, and Failure.


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



