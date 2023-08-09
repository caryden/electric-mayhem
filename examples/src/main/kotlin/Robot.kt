import subsystems.Subsystem


class Robot() : Subsystem() {
    val turret = Turret()
    init {
        subsystems.add(turret)
    }
}

