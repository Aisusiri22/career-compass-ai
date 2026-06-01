package com.example.data.model

data class Branch(
    val id: String,
    val name: String,
    val description: String,
    val careerPaths: List<String>
)

data class CareerPathDetail(
    val id: String,
    val name: String,
    val shortDesc: String,
    val whatIsIt: String,
    val whatTheyDo: String,
    val skills: List<String>,
    val advantages: List<String>,
    val challenges: List<String>,
    val futureScope: String,
    val topCompanies: List<String>,
    val typicalRoles: List<String>,
    val averageSalary: String,
    val difficulty: String, // Easy, Medium, High, Advanced
    val jobAvailability: String, // Moderate, High, Extreme
    val growthPotential: String, // Stable, Rapid, Exponential
    val workLifeBalance: String, // Good, Fair, Intense
    val roadmap: List<RoadmapYear>
)

data class RoadmapYear(
    val year: String,
    val topics: List<String>,
    val actions: List<String>
)

data class FutureMilestone(
    val timeframe: String,
    val title: String,
    val description: String,
    val skillsAcquired: List<String>,
    val targetProjects: List<String>
)

object CareerKnowledgeBase {
    val branches = listOf(
        Branch(
            id = "ECE",
            name = "Electronics & Communication Engineering",
            description = "Deals with semiconductor VLSI, microcontrollers, wireless networks, and embedded software systems.",
            careerPaths = listOf("VLSI", "Embedded Systems", "IoT", "Robotics", "Telecommunications", "AI/ML", "Software Development", "Semiconductor Industry")
        ),
        Branch(
            id = "CSE",
            name = "Computer Science & Engineering",
            description = "Centred on algorithms, cloud architecture, software design patterns, and systemic security.",
            careerPaths = listOf("Software Development", "AI/ML", "Cybersecurity", "Cloud Architecture", "Data Engineering", "Full Stack Web")
        ),
        Branch(
            id = "EE",
            name = "Electrical Engineering",
            description = "Involves power grids, control systems, electric vehicles, and heavy power industrial applications.",
            careerPaths = listOf("Power Systems", "Electric Vehicles (EV)", "Control Systems", "Industrial Automation", "Renewable Energy")
        ),
        Branch(
            id = "ME",
            name = "Mechanical Engineering",
            description = "Deals with structural design, thermodynamics, robotics locomotion, and fluid mechanics systems.",
            careerPaths = listOf("Robotics", "CAD/CAE Design", "Automotive Design", "Aerospace Technology", "HVAC Systems")
        )
    )

    val careerDetails = mapOf(
        "VLSI" to CareerPathDetail(
            id = "VLSI",
            name = "VLSI (Very Large Scale Integration)",
            shortDesc = "Design and integration of millions of transistors onto a single silicon semiconductor chip.",
            whatIsIt = "VLSI stands for Very Large Scale Integration. It is the process of creating integrated circuits (IC) by combining millions of transistors into a single microcircuit. This domain sits at the core of all computing devices, designing processors, RAM, and graphics cards.",
            whatTheyDo = "VLSI engineers write clean architecture code in Hardware Description Languages (HDL), test digital configurations on simulation engines, perform synthesis of physical components, and verify timing requirements of memory boards.",
            skills = listOf("Digital Electronics", "Verilog HDL", "SystemVerilog", "RTL Design", "STA (Static Timing Analysis)", "FPGA Architectures"),
            advantages = listOf("High entry & mature salaries", "Exponential domain authority", "Core electronic circuit specialization", "Shielded from general software layoffs"),
            challenges = listOf("Requires exceptionally strong math & physics basics", "Expensive development kits", "Long chip fabrication loops (mistakes cost millions)"),
            futureScope = "Highly strategic sector globally. Constant advancement in transistor scales (3nm/2nm physical boundaries) ensures dynamic long-term growth.",
            topCompanies = listOf("Intel", "NVIDIA", "Qualcomm", "AMD", "Texas Instruments", "TSMC"),
            typicalRoles = listOf("RTL Design Engineer", "Design Verification Engineer", "Physical Design Analyst", "STA Engineer"),
            averageSalary = "INR 8 - 25 LPA (Starting to Mid level)",
            difficulty = "Advanced",
            jobAvailability = "High",
            growthPotential = "Exponential",
            workLifeBalance = "Fair (tape-out cycles can be intense)",
            roadmap = listOf(
                RoadmapYear(
                    year = "Year 1",
                    topics = listOf("C Programming", "Basic Electronics", "Digital Logic Design"),
                    actions = listOf("Master binary representation & K-maps", "Build basic multiplexers on virtual digital trainers")
                ),
                RoadmapYear(
                    year = "Year 2",
                    topics = listOf("Verilog Language Essentials", "FPGA Basics", "Computer Architecture"),
                    actions = listOf("Code your first 8-bit Arithmetic Logic Unit (ALU) in Verilog", "Configure design loops on Xilinx or Quartus software tools")
                ),
                RoadmapYear(
                    year = "Year 3",
                    topics = listOf("RTL Design Patterns", "SystemVerilog Verification", "Static Timing Analysis"),
                    actions = listOf("Model pipeline register controllers", "Write testbenches with constrained-random stimulus")
                ),
                RoadmapYear(
                    year = "Year 4",
                    topics = listOf("Physical Design Synthesis", "Mini-project tape-out models", "Placement Preparation"),
                    actions = listOf("Apply for specialized internships at corporate chip design houses", "Solve gate-level netlist timing delay margins")
                )
            )
        ),
        "Embedded Systems" to CareerPathDetail(
            id = "Embedded Systems",
            name = "Embedded Systems",
            shortDesc = "The design of dedicated controller hardware with real-time software operating restraints.",
            whatIsIt = "Embedded systems combine customized circuit hardware with strict, highly optimized software. It sits in cars, drones, pacemaker chips, smartwatches, and industrial machinery.",
            whatTheyDo = "Embedded developers program directly on silicon microcontrollers in C/C++, design printed circuit board layouts, connect hardware communication buses (UART/I2C/SPI), and profile runtimes to save milliwatts of battery life.",
            skills = listOf("Embedded C / C++", "Microcontroller Architectures (ARM/STM32/ESP32)", "RTOS (Real-Time Operating Systems)", "I2C/SPI/UART Interface Protocols", "Oscilloscopes & PCB Design"),
            advantages = listOf("Direct physical product creations", "Excellent job security (physical hardware can't be purely simulated)", "Highly transferable hardware knowledge"),
            challenges = listOf("Requires debugging microscopic hardware signals", "Severe memory and power execution constraints"),
            futureScope = "Smart machines, IoT integrations, and Electric Vehicle controller grids are driving extreme embedded programming demand.",
            topCompanies = listOf("Bosch", "NXP Semiconductors", "STMicroelectronics", "Microchip Technology", "General Electric", "Tesla"),
            typicalRoles = listOf("Embedded Software Engineer", "Firmware Architect", "Hardware Design Engineer", "System Validation Specialist"),
            averageSalary = "INR 6 - 18 LPA",
            difficulty = "High",
            jobAvailability = "High",
            growthPotential = "Rapid",
            workLifeBalance = "Good",
            roadmap = listOf(
                RoadmapYear(
                    year = "Year 1",
                    topics = listOf("Basic Engineering Mathematics", "Electronics Devices", "Introduction to C Programming"),
                    actions = listOf("Deconstruct power supply circuitry", "Master custom memory allocations & pointer pointers in C")
                ),
                RoadmapYear(
                    year = "Year 2",
                    topics = listOf("8-bit & 32-bit Microcontroller Basics", "Assembly Code Basics", "GPIO Configurations"),
                    actions = listOf("Solder custom Arduino or ESP32 boards", "Program LED matrices and basic sound buzzers manually")
                ),
                RoadmapYear(
                    year = "Year 3",
                    topics = listOf("RTOS Basics (FreeRTOS)", "Device Driver Implementations", "SPI/I2C/CAN Interfacing"),
                    actions = listOf("Program parallel tasks using mutex locks", "Write custom register-level sensor drivers")
                ),
                RoadmapYear(
                    year = "Year 4",
                    topics = listOf("Industrial Capstone Projects", "PCB Layout Optimization", "Corporate Interview prep"),
                    actions = listOf("Create and order custom multi-layer circuit boards via EAGLE or KiCAD", "Target systems internships at premium automotive/aerospace firms")
                )
            )
        ),
        "IoT" to CareerPathDetail(
            id = "IoT",
            name = "IoT (Internet of Things)",
            shortDesc = "Connecting physical sensors, devices, and machinery to internet-backed web servers.",
            whatIsIt = "IoT coordinates sensor modules with local gate network equipment and cloud databases to gather, catalog, and interact with the physical physical environment.",
            whatTheyDo = "IoT engineers connect edge computing boxes, configure telemetry pipelines, secure radio waves (Wi-Fi/Bluetooth/LoRa), and write real-time cloud data pipelines.",
            skills = listOf("Sensors & Transducers", "LoRa / Zigbee / Bluetooth Protocols", "MQTT & HTTP Communication Profiles", "NodeJS / Python Integration", "Cloud Platforms (AWS IoT/Azure)"),
            advantages = listOf("Highly interdisciplinary and creative", "Direct cloud-to-hardware satisfaction", "Massive corporate smart-infrastructure investments"),
            challenges = listOf("Decisive system security hazards", "Managing hundreds of battery-centric remote nodes"),
            futureScope = "Global smart city initiatives, smart agribusiness, and connected digital factories assure expansive corporate market presence.",
            topCompanies = listOf("CISCO", "IBM", "Siemens", "Honeywell", "Intel", "Samsung"),
            typicalRoles = listOf("IoT Solutions Architect", "Smart Infrastructure Engineer", "IoT Security Specialist", "Cloud Integration Engineer"),
            averageSalary = "INR 5 - 15 LPA",
            difficulty = "Medium",
            jobAvailability = "High",
            growthPotential = "Rapid",
            workLifeBalance = "Good",
            roadmap = listOf(
                RoadmapYear(
                    year = "Year 1",
                    topics = listOf("Basic Programming (Python)", "Introduction to Computer Networks", "Sensor Principles"),
                    actions = listOf("Acquire basic TCP/IP structure", "Read analog sensor inputs using low-cost adapters")
                ),
                RoadmapYear(
                    year = "Year 2",
                    topics = listOf("Esp32 Programming", "Wi-Fi & Bluetooth Connective Routines", "Data serialization formatting (JSON)"),
                    actions = listOf("Expose simple local REST pages on micro-controllers", "Trigger physical switches using internet clicks")
                ),
                RoadmapYear(
                    year = "Year 3",
                    topics = listOf("MQTT Message Brokers", "AWS IoT Core Services", "Time-series database tracking"),
                    actions = listOf("Connect data pipelines via mosquito brokers", "Graph real-time temperatures on AWS dashboards")
                ),
                RoadmapYear(
                    year = "Year 4",
                    topics = listOf("IoT Fleet Security", "Battery Management, Low Power Modes", "Final Year Capstone Project"),
                    actions = listOf("Build self-healing, solar-connected, long-range tracking modules", "Practice algorithmic interview questions")
                )
            )
        ),
        "Robotics" to CareerPathDetail(
            id = "Robotics",
            name = "Robotics & Automation",
            shortDesc = "Integrating mechanical actuators, smart electric sensors, and computational code.",
            whatIsIt = "Robotics handles mechanical platforms capably, using control theories and sensor systems (LIDAR, cameras) to construct autonomous locomotion entities.",
            whatTheyDo = "Robotics engineers write kinematics calculations, configure Robot Operating System (ROS) routines, layout motor drive circuitry, and manage computer vision navigation.",
            skills = listOf("Control Systems", "ROS (Robot Operating System)", "Sensing & Actuation", "Python/C++ Kinematics", "Microcontroller Interfacing", "CAD/CAE design modeling"),
            advantages = listOf("Outstanding visual and mechanical accomplishments", "Frontier multidisciplinary intelligence", "Strong venture investments globally"),
            challenges = listOf("Capital-intensive components & specialized debugging", "Complex calculus-based spatial orientation mathematics"),
            futureScope = "Autonomous delivery vehicles, space missions, and warehouse robotic automation are driving high-end robotic recruitment.",
            topCompanies = listOf("Boston Dynamics", "ABB Robotics", "Fanuc", "Toyota", "Tesla", "DJI Drones"),
            typicalRoles = listOf("Robotics Software Engineer", "Control System Specialist", "Mechatronics Designer", "Automation Inspector"),
            averageSalary = "INR 7 - 20 LPA",
            difficulty = "Advanced",
            jobAvailability = "Moderate",
            growthPotential = "Rapid",
            workLifeBalance = "Good",
            roadmap = listOf(
                RoadmapYear(
                    year = "Year 1",
                    topics = listOf("Linear Algebra & Calculus", "Intro Mechanics", "C++ Fundamentals"),
                    actions = listOf("Program matrix rotation formulas", "Familiarize yourself with center of gravity calculations")
                ),
                RoadmapYear(
                    year = "Year 2",
                    topics = listOf("Feedback Controllers (PID)", "Arduino Motor Drivers", "Solidworks Cad Layouts"),
                    actions = listOf("Program self-balancing physical platforms", "Design customized robotic arm linkages")
                ),
                RoadmapYear(
                    year = "Year 3",
                    topics = listOf("ROS Nodes & Communication Blocks", "LiDAR & Computer Vision Basics", "Sensor Fusion"),
                    actions = listOf("Implement sensor filters on Raspberry Pi boards", "Map custom obstacles using LiDAR arrays")
                ),
                RoadmapYear(
                    year = "Year 4",
                    topics = listOf("Path Finding Algorithms (SLAM)", "Robotic Capstone Projects", "Hiring portfolios"),
                    actions = listOf("Draft dynamic navigation models", "Publish your physical robotic creations on YouTube and GitHub")
                )
            )
        ),
        "AI/ML" to CareerPathDetail(
            id = "AI/ML",
            name = "AI & Machine Learning",
            shortDesc = "Developing intelligence engines and statistical algorithms to analyze data patterns.",
            whatIsIt = "AI & Machine Learning is the science of training computers to generalize and predict results from datasets, replacing traditional static code with dynamic statistical models.",
            whatTheyDo = "ML Engineers clean raw data, train deep neural networks on GPU clusters, optimize models for mobile edge devices, and deploy scalable inference pipelines.",
            skills = listOf("Python & R", "PyTorch / TensorFlow", "Vector math & Applied Statistics", "SQL & Big Data Management", "FastAPI / Model Deployment"),
            advantages = listOf("Incredible starting salaries", "Highest research innovation budgets", "Very fast-moving and intellectually stimulating"),
            challenges = listOf("Extreme mathematics requirements", "Hype cycles and constant context shifts"),
            futureScope = "Generative AI, agentic systems, and intelligence-on-device are revolutionizing all industries. Long-term demand is solid for high-quality builders.",
            topCompanies = listOf("Google DeepMind", "Meta AI", "OpenAI", "Microsoft", "Amazon", "NVIDIA"),
            typicalRoles = listOf("Machine Learning Engineer", "Data Scientist", "Research Associate", "MLOps Engineer"),
            averageSalary = "INR 9 - 30 LPA",
            difficulty = "Advanced",
            jobAvailability = "Extreme",
            growthPotential = "Exponential",
            workLifeBalance = "Fair (compute deadlines can be tense)",
            roadmap = listOf(
                RoadmapYear(
                    year = "Year 1",
                    topics = listOf("Linear Algebra & Statistics", "Python Programming", "Pandas & Numpy Libs"),
                    actions = listOf("Solve multi-variable calculus sheets", "Clean and format messy survey data files")
                ),
                RoadmapYear(
                    year = "Year 2",
                    topics = listOf("Classical Machine Learning Principles", "Scikit-Learn models", "Database SQL"),
                    actions = listOf("Train linear regression and random forest systems", "Siphon relevant database charts using SQL queries")
                ),
                RoadmapYear(
                    year = "Year 3",
                    topics = listOf("Deep Learning (CNN/RNN/Transformers)", "PyTorch Training Pipelines", "Data cleaning"),
                    actions = listOf("Code your own digit class recognition model", "Fine-tune pretrained computer vision systems")
                ),
                RoadmapYear(
                    year = "Year 4",
                    topics = listOf("MLOps & Docker Deployment", "Gemini API Integrations", "Placement Coding Tracks"),
                    actions = listOf("Expose deep models via FastAPI containers", "Host fully functional inference setups on cloud providers")
                )
            )
        ),
        "Software Development" to CareerPathDetail(
            id = "Software Development",
            name = "Software Development Engineering (SDE)",
            shortDesc = "Designing scalable web apps, backend logic databases, and operating structures.",
            whatIsIt = "Software Development covers designing user-centric web/mobile layouts, high-velocity logic layers, and durable datastores to power global applications.",
            whatTheyDo = "SDEs write clean Kotlin, Java, or TypeScript, implement structural design patterns, design reliable REST/GraphQL interfaces, and configure automated CI/CD deployment pipelines.",
            skills = listOf("Data Structures & Algorithms (DSA)", "Object Oriented Design (OOD)", "Databases (SQL / MongoDB)", "Backend architectures (Spring / Node)", "System Design"),
            advantages = listOf("Largest job market in tech", "Vast range of resources & tutorials", "Excellent remote work opportunities"),
            challenges = listOf("Fierce entry-level developer competition", "Constant framework update exhaustion"),
            futureScope = "Every global company needs software. Software architecture remains fundamental block to all global tech operations.",
            topCompanies = listOf("Google", "Microsoft", "Amazon", "Meta", "Netflix", "Atlassian"),
            typicalRoles = listOf("Frontend Developer", "Backend Developer", "Full Stack Architect", "Android Engineer"),
            averageSalary = "INR 6 - 22 LPA",
            difficulty = "Medium",
            jobAvailability = "Extreme",
            growthPotential = "Stable",
            workLifeBalance = "Good",
            roadmap = listOf(
                RoadmapYear(
                    year = "Year 1",
                    topics = listOf("C++ / Java Syntax", "Object Oriented Concepts", "Basic HTML/CSS/JS"),
                    actions = listOf("Build static personal bio pages", "Master abstract encapsulation variables")
                ),
                RoadmapYear(
                    year = "Year 2",
                    topics = listOf("Data Structures (Trees/Graphs)", "SQL queries", "Github versioning"),
                    actions = listOf("Solve 150+ coding problems", "Collaborate on multi-branch repository tracks")
                ),
                RoadmapYear(
                    year = "Year 3",
                    topics = listOf("Web Frameworks (React/Node)", "REST APIs", "Jetpack Compose"),
                    actions = listOf("Develop a multi-view full-stack application", "Integrate authentication routers")
                ),
                RoadmapYear(
                    year = "Year 4",
                    topics = listOf("System Design & Scalability", "Coding interview patterns", "Placement sprints"),
                    actions = listOf("Design scalable Netflix/Uber architectural drafts", "Ace technical coding interview rounds")
                )
            )
        )
    )

    fun getPathsForBranch(branchId: String): List<CareerPathDetail> {
        val branch = branches.firstOrNull { it.id == branchId } ?: return emptyList()
        return branch.careerPaths.mapNotNull { careerDetails[it] }
    }

    val milestones = mapOf(
        "VLSI" to listOf(
            FutureMilestone("6 Months", "Fundamental Assembly Mastery", "Understand basic logic structures and master transistor physics simulation environments.", listOf("Digital Logic", "Transistor Basics"), listOf("CMOS Inverter Schematics")),
            FutureMilestone("1 Year", "Verilog Design Specialization", "Write synthesisable state controllers in Verilog and simulate clock systems.", listOf("Verilog HDL", "Signal Models"), listOf("8-bit Multiplier Engine")),
            FutureMilestone("2 Years", "SystemVerilog & Verification", "Gain entry-level structural verification skills using system test structures.", listOf("SystemVerilog", "Timing margins"), listOf("Dynamic UART/I2C Controller Verification")),
            FutureMilestone("Graduation", "Core Ready Hardware ASIC Designer", "Successfully design system architectures for VLSI firm placement.", listOf("STA Analysis", "RTL Coding"), listOf("Full Pipestage RISC-V Logic Core"))
        ),
        "Embedded Systems" to listOf(
            FutureMilestone("6 Months", "Peripheral Mastery", "Wire and interact with sensors using standard register protocols on microcontrollers.", listOf("Embedded C", "GPIO registers"), listOf("Self-correcting Thermostat Driver")),
            FutureMilestone("1 Year", "Device Communications", "Handle multi-channel communications without blocking processors.", listOf("SPI/I2C/UART", "Interrupt controllers"), listOf("Interrupt-driven GPS module reader")),
            FutureMilestone("2 Years", "Real-Time Processing (RTOS)", "Design clean concurrent tasks managed by real-time schedulers.", listOf("FreeRTOS", "Mutex/Semaphores"), listOf("Telemetry Drone Flight Controller")),
            FutureMilestone("Graduation", "Firmware Professional", "Build high-speed PCB layouts containing secure microcontroller networks.", listOf("PCB CAD Layouts", "Power optimizations"), listOf("Industrial Motor Monitor with CAN Bus"))
        ),
        "IoT" to listOf(
            FutureMilestone("6 Months", "Smart Nodes", "Program microprocessors to measure and broadcast telemetry.", listOf("ESP32 Basics", "API Calls"), listOf("Smart Smart-home light adapter")),
            FutureMilestone("1 Year", "Industrial Transport Protocols", "Transfer sensor streams using data message systems.", listOf("MQTT Broker", "WiFi Handshakes"), listOf("Local Environmental Sensor Monitor")),
            FutureMilestone("2 Years", "Cloud Telemetry Integrations", "Configure secure connections from remote boards to enterprise servers.", listOf("AWS IoT Core", "Database hooks"), listOf("Multi-node Agricultural Moisture Trackers")),
            FutureMilestone("Graduation", "IoT Solutions Director", "Establish robust sensor systems featuring end-to-end device security.", listOf("Firmware cryptography", "Docker containers"), listOf("Smart Industrial Facility Automation System"))
        ),
        "Robotics" to listOf(
            FutureMilestone("6 Months", "Motor control & CAD", "Learn to draw components and program simple motion feedback controllers.", listOf("CAD Modelling", "Microchip drivers"), listOf("Two-wheel Mobile Balancing Bot")),
            FutureMilestone("1 Year", "Locomotion Kinematics", "Program joint movements and trace paths mathematically.", listOf("Inverse Kinematics", "PID controllers"), listOf("4-Degree Robotic Arm Manipulator")),
            FutureMilestone("2 Years", "Robot Operations System (ROS)", "Leverage custom nodes to exchange spatial navigation details.", listOf("ROS nodes", "Linux administration"), listOf("Camera-guided Obstacle Avoiding Cart")),
            FutureMilestone("Graduation", "Autonomous Mechatronics Engineer", "Implement spatial mapping algorithms inside heavy robotic setups.", listOf("SLAM Algorithms", "Sensor Fusion"), listOf("Industrial Autonomous Warehouse Rover"))
        ),
        "AI/ML" to listOf(
            FutureMilestone("6 Months", "Data Wrangling & Stats", "Format, clean, and run mathematical models on custom datasets.", listOf("Python Pandas", "Calculus formulas"), listOf("Academic Student Performance Classifier")),
            FutureMilestone("1 Year", "Classical Algorithms", "Code customized models to perform structural predictions.", listOf("Scikit-Learn", "Regression models"), listOf("House Valuation Estimate Engine")),
            FutureMilestone("2 Years", "Neural Networks & Deep Learning", "Formulate neural layers to classify visual or textual data details.", listOf("PyTorch", "TensorFlow"), listOf("Medical Imaging Tumor Segmenter")),
            FutureMilestone("Graduation", "Deep Intelligent MLOps Architect", "Deploy highly parallelized inference logic blocks onto active web servers.", listOf("Model serving", "Docker/FastAPI"), listOf("Smart RAG Chatbot powered by fine-tuned models"))
        ),
        "Software Development" to listOf(
            FutureMilestone("6 Months", "Logic & UI basics", "Master core programming syntax and craft interactive personal page structures.", listOf("Java/Kotlin Syntax", "HTML/CSS layouts"), listOf("Responsive Personal Bio Portfolio")),
            FutureMilestone("1 Year", "Advanced Structures & DBs", "Sort variables and perform database updates seamlessly.", listOf("Trees & Graphs", "SQL Databases"), listOf("Multi-user Todo Tracking System")),
            FutureMilestone("2 Years", "Framework architecture", "Write full-stack services connecting frontend widgets with database collections.", listOf("React/NodeJS", "Jetpack Compose"), listOf("Collaborative Project Planner with Chat Features")),
            FutureMilestone("Graduation", "Distributed Systems Engineer", "Develop scalable cloud architectures resisting sudden user surges.", listOf("System Design", "Cloud setups"), listOf("Scalable High-Performance Streaming Platform"))
        )
    )

    fun getComparison(item1: String, item2: String): Pair<CareerPathDetail?, CareerPathDetail?> {
        return Pair(careerDetails[item1], careerDetails[item2])
    }
}
