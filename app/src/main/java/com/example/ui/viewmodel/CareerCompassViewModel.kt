package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.api.Content
import com.example.data.api.GenerateContentRequest
import com.example.data.api.Part
import com.example.data.api.RetrofitClient
import com.example.data.db.*
import com.example.data.model.CareerKnowledgeBase
import com.example.data.model.CareerPathDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class CareerMatch(
    val pathName: String,
    val matchPercentage: Int,
    val reason: String
)

class CareerCompassViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val profileDao = database.profileDao()
    private val skillProgressDao = database.skillProgressDao()
    private val studyPlanDao = database.studyPlanDao()
    private val chatMessageDao = database.chatMessageDao()

    // --- Tab Navigation Context ---
    val currentTab = MutableStateFlow(0) // 0: Dashboard, 1: Explore, 2: Recommendation, 3: Roadmap & Planner, 4: AI Mentor

    // --- Profile State ---
    val profile: StateFlow<ProfileEntity?> = profileDao.getProfile().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // --- Active Career Selection ---
    val activeCareerPath = MutableStateFlow("VLSI")

    val activeCareerDetails: StateFlow<CareerPathDetail> = activeCareerPath
        .map { path ->
            CareerKnowledgeBase.careerDetails[path] ?: CareerKnowledgeBase.careerDetails["VLSI"]!!
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CareerKnowledgeBase.careerDetails["VLSI"]!!
        )

    // --- Skill Progress Tracker Reactive States ---
    val activePathSkills: StateFlow<List<SkillProgressEntity>> = activeCareerPath
        .flatMapLatest { path ->
            skillProgressDao.getProgressForPath(path)
        }
        .onEach { list ->
            // If empty, initialize skills in database
            val path = activeCareerPath.value
            if (list.isEmpty()) {
                val detail = CareerKnowledgeBase.careerDetails[path]
                if (detail != null) {
                    viewModelScope.launch {
                        detail.skills.forEach { skill ->
                            skillProgressDao.saveProgress(
                                SkillProgressEntity(
                                    careerPath = path,
                                    skillName = skill,
                                    isCompleted = false
                                )
                            )
                        }
                    }
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val activePathProgressPercentage: StateFlow<Int> = activePathSkills
        .map { list ->
            if (list.isEmpty()) 0
            else {
                val completed = list.count { it.isCompleted }
                (completed * 100) / list.size
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    // --- Comparative Diagnostics ---
    val comparePath1 = MutableStateFlow("VLSI")
    val comparePath2 = MutableStateFlow("Embedded Systems")

    // --- Study Planner Input/Output ---
    val dailyStudyHours = MutableStateFlow(4)
    
    val activeStudyPlan: StateFlow<StudyPlanEntity?> = activeCareerPath
        .flatMapLatest { path ->
            studyPlanDao.getStudyPlan(path)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // --- Chat Messages and AI Context ---
    val chatMessages: StateFlow<List<ChatMessageEntity>> = chatMessageDao.getAllMessages().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val isChatLoading = MutableStateFlow(false)

    // --- AI Career Recommendation Engine ---
    val interestCoding = MutableStateFlow(false)
    val interestElectronics = MutableStateFlow(false)
    val interestHardware = MutableStateFlow(false)
    val interestResearch = MutableStateFlow(false)
    val interestMath = MutableStateFlow(false)

    val isRecommendationLoading = MutableStateFlow(false)
    val recommendedPaths = MutableStateFlow<List<CareerMatch>>(emptyList())

    init {
        // Pre-initialize a template student profile if DB is completely empty
        viewModelScope.launch {
            profileDao.getProfile().firstOrNull()?.let {
                // profile exists, do nothing
            } ?: run {
                profileDao.saveProfile(
                    ProfileEntity(
                        id = 1,
                        name = "Sarah Jenkins",
                        branch = "Electronics & Communication Engineering",
                        collegeYear = "2nd Year",
                        interests = "Electronics, High-speed hardware circuits, Microcontrollers",
                        skills = "Basic C, Digital Logic Gates",
                        goals = "Targeting high-paying Core hardware placement design jobs."
                    )
                )
            }
        }
    }

    // --- DB Mutators ---
    fun updateProfile(name: String, branch: String, year: String, interests: String, skills: String, goals: String) {
        viewModelScope.launch {
            profileDao.saveProfile(
                ProfileEntity(
                    id = 1,
                    name = name,
                    branch = branch,
                    collegeYear = year,
                    interests = interests,
                    skills = skills,
                    goals = goals
                )
            )
        }
    }

    fun toggleSkillCompletion(skillName: String, isChecked: Boolean) {
        viewModelScope.launch {
            skillProgressDao.updateSkillStatus(
                careerPath = activeCareerPath.value,
                skillName = skillName,
                isCompleted = isChecked
            )
        }
    }

    // --- Study Plan Generator ---
    fun generateStudyPlan(hours: Int) {
        val path = activeCareerPath.value
        viewModelScope.launch {
            val daily = "Dedicate $hours hours to learning the fundamentals of $path.\n" +
                    "- **First 1.5 Hours**: Technical conceptual reading & circuit simulations.\n" +
                    "- **Next 1.5 Hours**: Active coding, assembly, or hardware configuration projects.\n" +
                    "- **Remaining Time**: Solve placement interview MCQs or write timing sheets."
            
            val weekly = "Total Weekly Target: ${hours * 7} hours of targeted learning.\n" +
                    "- **Mon & Tue**: Master theoretical gate designs, layout diagrams, or logic stacks.\n" +
                    "- **Wed & Thu**: Practical simulations, logic writing, or testbench scripting.\n" +
                    "- **Friday**: Debugging and resolving active compile/circuit errors.\n" +
                    "- **Sat & Sun**: Complete 1 mini-project and update your progress portfolio."

            val monthly = "Focus Goal: High-velocity understanding of $path.\n" +
                    "- **Week 1**: Strong mathematical concepts and foundational signal diagrams.\n" +
                    "- **Week 2**: Practical tool setup (Simulator modules, CAD systems, or IDE configuration).\n" +
                    "- **Week 3**: Intermediate logic assemblies (Register transfers, multiplexer loops, and APIs).\n" +
                    "- **Week 4**: Interface debugging, timing analysis, and project packaging."

            studyPlanDao.saveStudyPlan(
                StudyPlanEntity(
                    careerPath = path,
                    dailyHours = hours,
                    dailyPlan = daily,
                    weeklyPlan = weekly,
                    monthlyPlan = monthly
                )
            )
        }
    }

    // --- Recommendation Logic ---
    fun runRecommendationEngine() {
        isRecommendationLoading.value = true
        viewModelScope.launch {
            // Algorithmic foundation first, fallback/guided offline matching
            val coding = interestCoding.value
            val electronics = interestElectronics.value
            val hardware = interestHardware.value
            val research = interestResearch.value
            val math = interestMath.value

            val calculations = mutableListOf<CareerMatch>()

            // Score matches for our 6 main paths based on interest answers
            // 1. VLSI
            var vlsiScore = 30
            if (electronics) vlsiScore += 25
            if (hardware) vlsiScore += 25
            if (math) vlsiScore += 15
            if (research) vlsiScore += 5
            calculations.add(
                CareerMatch(
                    "VLSI", 
                    vlsiScore.coerceAtMost(100), 
                    "Excellent alignment. VLSI requires elite hardware understandings and electronics hardware foundations to synthesise microprocessors."
                )
            )

            // 2. Embedded Systems
            var embedScore = 30
            if (electronics) embedScore += 20
            if (hardware) embedScore += 30
            if (coding) embedScore += 20
            calculations.add(
                CareerMatch(
                    "Embedded Systems", 
                    embedScore.coerceAtMost(100), 
                    "Superb match. Embedded engineering is the convergence of hardware peripherals, circuit boards, and real-time microcontroller programming in C."
                )
            )

            // 3. AI/ML
            var aiScore = 20
            if (coding) aiScore += 30
            if (math) aiScore += 30
            if (research) aiScore += 20
            calculations.add(
                CareerMatch(
                    "AI/ML", 
                    aiScore.coerceAtMost(100), 
                    "Strong computational focus. Artificial intelligence relies on deep mathematical equations, PyTorch coding, and statistical model tuning."
                )
            )

            // 4. Software Development
            var sdeScore = 30
            if (coding) sdeScore += 50
            if (math) sdeScore += 20
            calculations.add(
                CareerMatch(
                    "Software Development", 
                    sdeScore.coerceAtMost(100), 
                    "Excellent core programming match. Ideal for building scalable internet web applications, APIs, databases, and structured services."
                )
            )

            // 5. IoT
            var iotScore = 30
            if (electronics) iotScore += 20
            if (coding) iotScore += 25
            if (hardware) iotScore += 25
            calculations.add(
                CareerMatch(
                    "IoT", 
                    iotScore.coerceAtMost(100), 
                    "Broad mechatronic integration. Combines physical sensors with local mesh networks and cloud databases to construct smart buildings."
                )
            )

            // 6. Robotics
            var robotScore = 20
            if (hardware) robotScore += 25
            if (electronics) robotScore += 20
            if (coding) robotScore += 20
            if (math) robotScore += 15
            calculations.add(
                CareerMatch(
                    "Robotics", 
                    robotScore.coerceAtMost(100), 
                    "Intense mechatronic core. Integrates mechanical actuators, microcontroller feedback PID loops, and camera navigation."
                )
            )

            // Sort matches descending by percentage
            calculations.sortByDescending { it.matchPercentage }

            // Simulate small compute delay
            withContext(Dispatchers.IO) {
                Thread.sleep(1200)
            }
            recommendedPaths.value = calculations
            isRecommendationLoading.value = false
        }
    }

    // --- AI Mentor Chat Engine ---
    fun sendChatMessage(msgText: String) {
        if (msgText.trim().isEmpty()) return

        viewModelScope.launch {
            // Save user message immediately to Room Database
            chatMessageDao.insertMessage(
                ChatMessageEntity(role = "user", content = msgText)
            )

            isChatLoading.value = true

            // Formulate current conversation history for the API call
            val currentMessages = chatMessages.value
            val historyParts = currentMessages.map { 
                Part(text = "${if (it.role == "user") "Student: " else "Mentor: "}${it.content}") 
            } + Part(text = "Student: $msgText")

            val promptText = historyParts.joinToString("\n") { it.text ?: "" }

            // Define custom instructions to keep responses clear, encouraging, structured, and short
            val systemInstructions = "You are 'Career Compass AI', an inspiring, supportive, and remarkably clear engineering career mentor. Your tagline is 'Choose the Right Path, Not Just Any Path.' " +
                    "Your mission is to guide students clearly through VLSI, Embedded Systems, IoT, Robotics, AI/ML, and general SDE questions. " +
                    "Always answer in simple, encouraging, structured, and beginner-friendly formatting. Use bullet points for steps. " +
                    "Keep explanations highly readable and direct. Avoid overwhelming blocks of complex variables."

            val apiKey = BuildConfig.GEMINI_API_KEY

            // Is the developer API Key correctly initialized in the build configuration?
            val isKeyPlaceholder = apiKey == "MY_GEMINI_API_KEY" || apiKey.trim().isEmpty()

            if (isKeyPlaceholder) {
                // Elegant local guided AI mentor simulation to support robust offline demonstration!
                withContext(Dispatchers.IO) {
                    Thread.sleep(1500)
                }
                
                val lowerMsg = msgText.lowercase()
                val responseContent = when {
                    lowerMsg.contains("vlsi") -> {
                        "**Mentor Response (Offline Mode):**\n\nVLSI is an incredible core electronics specialization! It is all about squeezing millions of transistors onto a microchip.\n\n" +
                                "Here is how to start step-by-step:\n" +
                                "1. **Build foundations**: Excel in Digital Logic gates and K-maps.\n" +
                                "2. **Study Verilog / VHDL**: Adopt HDLs to write register-transfer level logic.\n" +
                                "3. **Simulate**: Work with simulators like ModelSim or EDA tools.\n" +
                                "4. **Learn Physical Design**: Master Synthesis & STA (Static Timing Analysis).\n\n" +
                                "💡 *Tip:* To unlock live, fully-personalized AI brainstorming with me, you can input your private API Key in the **Secrets Panel in Android AI Studio**!"
                    }
                    lowerMsg.contains("embed") -> {
                        "**Mentor Response (Offline Mode):**\n\nEmbedded Systems is where physical hardware shakes hands with software code! You write firmware directly onto microcontrollers to control widgets.\n\n" +
                                "Key Milestones:\n" +
                                "1. **Learn C Programming**: Ensure you are flawless at pointer arithmetic and bitwise utilities.\n" +
                                "2. **Grab an ESP32 or Arduino**: Solder boards and toggle register timers.\n" +
                                "3. **Study RTOS**: Understand how task schedulers coordinate parallel tasks safely.\n\n" +
                                "💡 *Tip:* To unlock live, fully-personalized AI brainstorming with me, you can input your private API Key in the **Secrets Panel in Android AI Studio**!"
                    }
                    lowerMsg.contains("math") || lowerMsg.contains("coding") -> {
                        "**Mentor Response (Offline Mode):**\n\nGreat question! \n- For **VLSI & Embedded Systems**: Hardware C and Verilog coding are vital, while mathematical binary operations are highly emphasized.\n- For **AI/ML**: Python and advanced linear algebra/statistics are mandatory.\n- For **Pure SDE**: High-velocity problem-solving and Data Structures & Algorithms (DSA) take precedence.\n\n" +
                                "💡 *Tip:* To unlock live, fully-personalized AI brainstorming with me, you can input your private API Key in the **Secrets Panel in Android AI Studio**!"
                    }
                    else -> {
                        "**Mentor Response (Offline Mode):**\n\nI am right here to help you navigate engineering options! Whether exploring ECE core paths like VLSI, embedded hardware, wireless communication stacks, or transitioning safely into SDE/AI roles, structure and patience are key.\n\n" +
                                "Ask me specific details about:\n" +
                                "- 'How do I start with VLSI?'\n" +
                                "- 'Is coding mandatory for Embedded Systems?'\n" +
                                "- 'Recommend a simple roadmap.'\n\n" +
                                "💡 *Tip:* To unlock live, fully-personalized AI brainstorming with me, you can input your private API Key in the **Secrets Panel in Android AI Studio**!"
                    }
                }

                chatMessageDao.insertMessage(
                    ChatMessageEntity(role = "model", content = responseContent)
                )
                isChatLoading.value = false
            } else {
                // Call live Gemini REST Client safely using standard coroutines
                try {
                    val request = GenerateContentRequest(
                        contents = listOf(
                            Content(parts = listOf(Part(text = "$systemInstructions\n\nPrevious conversation:\n$promptText\n\nMentor response:")))
                        )
                    )
                    
                    val response = RetrofitClient.service.generateContent(apiKey, request)
                    val reply = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                        ?: "I was unable to formulate a response. Let me digest this further, or feel free to re-order the question query."

                    chatMessageDao.insertMessage(
                        ChatMessageEntity(role = "model", content = reply)
                    )
                } catch (e: Exception) {
                    val errReply = "My communication antenna is currently experiencing network delays: ${e.localizedMessage}. Let me know if you would like to consult my pre-curated offline repository of VLSI and Embedded system resources!"
                    chatMessageDao.insertMessage(
                        ChatMessageEntity(role = "model", content = errReply)
                    )
                } finally {
                    isChatLoading.value = false
                }
            }
        }
    }

    fun clearChatHistory() {
        viewModelScope.launch {
            chatMessageDao.clearHistory()
        }
    }
}
