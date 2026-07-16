package com.example.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

// --- Data Models for Non-DB elements ---

data class LawyerFirm(
    val name: String,
    val rating: Float,
    val reviewsCount: Int,
    val specialty: String,
    val location: String, // City/State
    val phone: String,
    val address: String,
    val reviews: List<UserReview>
)

data class UserReview(
    val reviewerName: String,
    val rating: Int,
    val comment: String
)

data class LawBookItem(
    val id: String,
    val title: String,
    val category: String, // "Federal" or "State"
    val stateName: String? = null, // if category is "State"
    val fullText: String,
    val keyFacts: String,
    val loopholes: String,
    val citation: String
)

data class LegalQuizQuestion(
    val question: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val explanation: String
)

data class LawClass(
    val id: String,
    val title: String,
    val category: String,
    val videoUrl: String, // YouTube/educational URL or placeholder
    val summary: String,
    val detailedText: String,
    val interactiveQuiz: List<LegalQuizQuestion>
)

class LawRepository(private val context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val dao = database.lawDao()
    private val geminiRepo = GeminiRepository()

    // --- DB Queries ---
    val allCases: Flow<List<CaseFile>> = dao.getAllCasesFlow()
    val allReminders: Flow<List<CourtReminder>> = dao.getAllRemindersFlow()
    val highScores: Flow<List<QuizHighScore>> = dao.getHighScoresFlow()
    val avatarConfig: Flow<AvatarConfig?> = dao.getAvatarConfigFlow()
    val lawAlerts: Flow<List<LawChangeAlert>> = dao.getLawAlertsFlow()

    suspend fun saveCase(caseFile: CaseFile) = dao.insertCase(caseFile)
    suspend fun deleteCase(id: Int) = dao.deleteCaseById(id)

    suspend fun saveReminder(reminder: CourtReminder) = dao.insertReminder(reminder)
    suspend fun deleteReminder(id: Int) = dao.deleteReminderById(id)

    suspend fun saveHighScore(score: QuizHighScore) = dao.insertHighScore(score)

    suspend fun saveAvatarConfig(config: AvatarConfig) = dao.insertAvatarConfig(config)
    suspend fun getAvatarConfig(): AvatarConfig = dao.getAvatarConfig() ?: AvatarConfig()

    suspend fun saveLawAlert(alert: LawChangeAlert) = dao.insertLawAlert(alert)
    suspend fun markAlertAsRead(id: Int) = dao.markAlertAsRead(id)

    // --- Gemini Calls ---
    suspend fun generateAiFeedback(prompt: String, systemPrompt: String? = null): String {
        return geminiRepo.generateLegalFeedback(prompt, systemPrompt)
    }

    suspend fun analyzeDocument(base64Image: String, mimeType: String, description: String): String {
        return geminiRepo.analyzeDocumentLegality(base64Image, mimeType, description)
    }

    // --- Initial Seed ---
    suspend fun seedInitialData() {
        // Pre-configure standard avatar if not exists
        if (dao.getAvatarConfig() == null) {
            dao.insertAvatarConfig(AvatarConfig())
        }

        // Seed some law alerts if empty
        val currentAlerts = dao.getLawAlertsFlow().firstOrNull()
        if (currentAlerts.isNullOrEmpty()) {
            dao.insertLawAlert(
                LawChangeAlert(
                    title = "New York Rent Stabilization Adjustment",
                    state = "New York",
                    description = "The Rent Guidelines Board approved a 2.75% increase for one-year leases and 3.2% for two-year leases on stabilized apartments. Enforcement starts October 2026."
                )
            )
            dao.insertLawAlert(
                LawChangeAlert(
                    title = "California Right to Repair Act",
                    state = "California",
                    description = "SB 244 takes full effect, requiring electronics manufacturers to provide parts, tools, and manuals on fair terms to independent repair shops and consumers for up to 7 years."
                )
            )
            dao.insertLawAlert(
                LawChangeAlert(
                    title = "Federal Small Business Corporate Transparency Act Update",
                    state = "Federal",
                    description = "FinCEN extends reporting timelines for small businesses. All entities formed in 2026 now have 90 days to register beneficial ownership information to avoid civil penalties."
                )
            )
        }
    }

    // --- Mock Lawyer Search Engine (with Reviews) ---
    fun searchLawyers(locationQuery: String): List<LawyerFirm> {
        val query = locationQuery.trim().lowercase()
        val allFirms = listOf(
            LawyerFirm(
                name = "Liberty Defense Law Group",
                rating = 4.9f,
                reviewsCount = 124,
                specialty = "Criminal Defense & Civil Rights",
                location = "Los Angeles, CA",
                phone = "(213) 555-0198",
                address = "555 Flower St, Los Angeles, CA 90071",
                reviews = listOf(
                    UserReview("Sarah J.", 5, "Saved my life! Demanded officer's bodycam footage and proved they conducted an illegal search. Case dismissed!"),
                    UserReview("Michael K.", 5, "Extremely professional. Explains things clearly. Best criminal lawyer in CA.")
                )
            ),
            LawyerFirm(
                name = "Golden Gate Tenant Defenders",
                rating = 4.8f,
                reviewsCount = 85,
                specialty = "Landlord-Tenant & Housing Law",
                location = "San Francisco, CA",
                phone = "(415) 555-0143",
                address = "120 Pine St, San Francisco, CA 94111",
                reviews = listOf(
                    UserReview("Elena R.", 5, "Helped me fight a wrongful eviction. Proved the landlord failed to provide proper 60-day notice under CA law."),
                    UserReview("Devon B.", 4, "Strong legal advice. Took away all the stress of dealing with an unfair rent hike.")
                )
            ),
            LawyerFirm(
                name = "Empire State Litigators LLP",
                rating = 4.9f,
                reviewsCount = 203,
                specialty = "Contract disputes & Civil Law",
                location = "New York, NY",
                phone = "(212) 555-7890",
                address = "405 Lexington Ave, New York, NY 10174",
                reviews = listOf(
                    UserReview("Robert T.", 5, "Incredible contract review. Found a crucial indemnity loophole that saved our tech startup."),
                    UserReview("Amanda M.", 5, "Aggressive, smart, and always ahead of the prosecutor. Absolute giants.")
                )
            ),
            LawyerFirm(
                name = "Metro NY Civil Rights Coalition",
                rating = 4.7f,
                reviewsCount = 67,
                specialty = "Police Misconduct & Civil Rights",
                location = "Brooklyn, NY",
                phone = "(718) 555-0112",
                address = "182 Flatbush Ave, Brooklyn, NY 11217",
                reviews = listOf(
                    UserReview("Marcus W.", 5, "Fought for me after a wrongful arrest. They really care about defending citizens' constitutional rights."),
                    UserReview("Carlos S.", 4, "Reliable team, always answered my calls and kept me updated.")
                )
            ),
            LawyerFirm(
                name = "Texas Patriot Defense & Associates",
                rating = 4.9f,
                reviewsCount = 142,
                specialty = "Criminal Law & Self-Defense",
                location = "Austin, TX",
                phone = "(512) 555-8833",
                address = "300 W 6th St, Austin, TX 78701",
                reviews = listOf(
                    UserReview("Colt M.", 5, "Expert on Texas Stand Your Ground laws. Helped clear my name completely."),
                    UserReview("Janice H.", 5, "Very responsive, tough in court, and knows the federal rules inside and out.")
                )
            ),
            LawyerFirm(
                name = "Lonestar Family & Business Attorneys",
                rating = 4.6f,
                reviewsCount = 94,
                specialty = "Family & Corporate Law",
                location = "Houston, TX",
                phone = "(713) 555-9011",
                address = "1000 Louisiana St, Houston, TX 77002",
                reviews = listOf(
                    UserReview("George L.", 5, "Efficient divorce representation. Secured my parental rights perfectly."),
                    UserReview("Susan P.", 4, "Assisted in setting up our business LLC and drafting standard employment agreements.")
                )
            ),
            LawyerFirm(
                name = "Sunshine State Legal Aid Group",
                rating = 4.7f,
                reviewsCount = 110,
                specialty = "Personal Injury & Consumer Protection",
                location = "Miami, FL",
                phone = "(305) 555-2244",
                address = "801 Brickell Ave, Miami, FL 33131",
                reviews = listOf(
                    UserReview("Theresa D.", 5, "Got me a full settlement after an auto crash. Couldn't thank them enough!"),
                    UserReview("Alex G.", 4, "Knowledgeable attorneys. Fought the insurance company successfully.")
                )
            )
        )

        if (query.isEmpty()) {
            // Default: return all sorted by ratings
            return allFirms.sortedByDescending { it.rating }
        }

        // Filter based on city, state code, address, or zip
        return allFirms.filter {
            it.location.lowercase().contains(query) ||
                    it.address.lowercase().contains(query) ||
                    it.specialty.lowercase().contains(query)
        }.sortedByDescending { it.rating }
    }

    // --- Static US Law Books ---
    val lawBooks: List<LawBookItem> = listOf(
        LawBookItem(
            id = "fed_const_1",
            title = "The First Amendment (Freedom of Speech & Assembly)",
            category = "Federal",
            fullText = "Congress shall make no law respecting an establishment of religion, or prohibiting the free exercise thereof; or abridging the freedom of speech, or of the press; or the right of the people peaceably to assemble, and to petition the Government for a redress of grievances.",
            keyFacts = "Protects citizens from government censorship of speech, peaceful protests, media reporting, and religious expressions. Does not protect private sector employment actions or 'fighting words' inciting immediate violence.",
            loopholes = "1. Public Forums: Audits or recordings on public sidewalks are heavily protected, even if officers allege 'loitering' or 'disturbing the peace'.\n2. Symbolic Speech: Wearing certain clothing or displaying banners are fully protected forms of communication.",
            citation = "U.S. Const. amend. I"
        ),
        LawBookItem(
            id = "fed_const_4",
            title = "The Fourth Amendment (Search and Seizure)",
            category = "Federal",
            fullText = "The right of the people to be secure in their persons, houses, papers, and effects, against unreasonable searches and seizures, shall not be violated, and no Warrants shall issue, but upon probable cause, supported by Oath or affirmation, and particularly describing the place to be searched, and the persons or things to be seized.",
            keyFacts = "Protects reasonable expectations of privacy. Requires police to obtain a judicial warrant based on probable cause for physical searches, phone data, or entering homes, unless explicit exceptions apply.",
            loopholes = "1. Plain View Doctrine: If evidence is in plain sight (e.g., on the car seat), officers don't need a warrant to seize it.\n2. Consent: Police often ask 'Do you mind if I check?' which is a voluntary waiver of your 4th Amendment. ALWAYS say clearly: 'I do not consent to any searches.'\n3. Terry Stop: Police can pat down outer clothing if they have reasonable, articulable suspicion of weapons.",
            citation = "U.S. Const. amend. IV"
        ),
        LawBookItem(
            id = "fed_const_5",
            title = "The Fifth Amendment (Self-Incrimination & Due Process)",
            category = "Federal",
            fullText = "No person shall... be compelled in any criminal case to be a witness against himself, nor be deprived of life, liberty, or property, without due process of law...",
            keyFacts = "Guarantees the right to remain silent when questioned by law enforcement and protects against double jeopardy and deprivation of liberty without fair trial procedures.",
            loopholes = "1. Custodial Interrogation: Miranda warnings are only required AFTER an arrest and during active interrogation. Anything said voluntarily BEFORE arrest can be used. Silence must be asserted out loud: 'I am invoking my right to remain silent, and I want an attorney.'",
            citation = "U.S. Const. amend. V"
        ),
        LawBookItem(
            id = "ca_tenant_1",
            title = "California Civil Code § 1946.1 (Renters' Protection / Eviction)",
            category = "State",
            stateName = "California",
            fullText = "Requires a landlord to provide a 60-day written notice to terminate a residential tenancy if the tenant has resided in the dwelling for a year or more, or 30 days if less than a year.",
            keyFacts = "Landlords cannot arbitrarily evict long-term tenants without precise compliance. California's Tenant Protection Act (AB 1482) also mandates 'Just Cause' reasons for evicting qualified tenants.",
            loopholes = "1. Improper Notice: If notice is sent via text message or email (without explicit lease agreement for electronic service), or lacks exact required statutory language, the eviction lawsuit (Unlawful Detainer) is defective and can be thrown out of court.\n2. Retaliation: It is illegal to issue a termination notice within 180 days of a tenant reporting habitability defects.",
            citation = "Cal. Civ. Code § 1946.1"
        ),
        LawBookItem(
            id = "ny_tenant_1",
            title = "New York Real Property Law § 226-C (Tenant Notice)",
            category = "State",
            stateName = "New York",
            fullText = "Requires landlords to provide 30, 60, or 90 days of written notice for non-renewal or rent increases greater than 5%, based on length of lease (30 days for <1 year, 60 days for 1-2 years, 90 days for 2+ years).",
            keyFacts = "Applies to both rent-stabilized and free-market apartments across New York state. Failure to give notice makes the current lease automatically extend at the original rate.",
            loopholes = "1. Late Notice: If a landlord gives late notice (e.g., 15 days before lease ends on a 2-year lease), the rent increase or termination cannot take effect until a full 90 days after notice was served. The tenant can pay original rent in the interim.",
            citation = "N.Y. Real Prop. Law § 226-c"
        ),
        LawBookItem(
            id = "tx_defense_1",
            title = "Texas Penal Code § 9.32 (Deadly Force in Defense)",
            category = "State",
            stateName = "Texas",
            fullText = "A person is justified in using deadly force against another if they reasonably believe it is immediately necessary to protect against the other's use of unlawful deadly force, or to prevent kidnapping, murder, sexual assault, or robbery.",
            keyFacts = "Commonly referred to as the Texas 'Stand Your Ground' law. There is no duty to retreat if the actor has a legal right to be present, did not provoke the person, and is not engaged in criminal activity.",
            loopholes = "1. Presumption of Reasonableness: The law presumes the user's belief was reasonable if the intruder unlawfully entered or attempted to enter their occupied habitation, vehicle, or workplace by force.\n2. Provocation: If the defender provoked the encounter, Stand Your Ground is negated, forcing a standard evaluation of self-defense.",
            citation = "Tex. Penal Code § 9.32"
        )
    )

    // --- Interactive Quiz Questions ---
    val quizQuestions: List<LegalQuizQuestion> = listOf(
        LegalQuizQuestion(
            question = "If a police officer pulls you over and asks: 'Do you mind if I check your trunk?', what is the most legally protective answer?",
            options = listOf(
                "Sure, I have nothing to hide.",
                "No, you can't search unless you find something.",
                "I do not consent to any searches.",
                "I will open it for you, sir."
            ),
            correctOptionIndex = 2,
            explanation = "Saying 'I do not consent to any searches' clearly asserts your 4th Amendment rights. Officers often ask in a casual way to bypass the warrant or probable cause requirements. If you consent, anything they find is admissible!"
        ),
        LegalQuizQuestion(
            question = "When does an officer have to read you your Miranda Rights (the right to remain silent, etc.)?",
            options = listOf(
                "The exact moment they pull you over in traffic.",
                "Only when you are in police custody AND being actively interrogated.",
                "Immediately upon placing handcuffs on you.",
                "Whenever they ask for your ID card."
            ),
            correctOptionIndex = 1,
            explanation = "Miranda Rights are strictly tied to 'Custodial Interrogation'. Handcuffs mean you are in custody, but warnings are only legally required if they start questioning you about the crime. Voluntary statements made prior are fully admissible."
        ),
        LegalQuizQuestion(
            question = "Under California's Tenant Protection Act (AB 1482), how much notice is required for eviction of a tenant of over 1 year?",
            options = listOf(
                "A verbal 10-day notice.",
                "A written 30-day notice.",
                "A written 60-day notice with declared Just Cause.",
                "A written 15-day notice with a certified letter."
            ),
            correctOptionIndex = 2,
            explanation = "Long-term tenants of over a year in California are protected by Just Cause. The landlord must provide a 60-day written notice specifying a legal, valid reason (just cause), like non-payment, lease breach, or owner move-in."
        ),
        LegalQuizQuestion(
            question = "Can a private sector employer fire you for posting a political opinion on personal social media?",
            options = listOf(
                "No, that violates the 1st Amendment right to free speech.",
                "Yes, because the 1st Amendment only protects you from GOVERNMENT censorship, and most states are 'at-will' employment.",
                "No, because political belief is a federally protected class.",
                "Yes, but only if the post was written during work hours."
            ),
            correctOptionIndex = 1,
            explanation = "The 1st Amendment states 'CONGRESS shall make no law...'. It only limits government entities. Private employers can legally fire employees for social media posts, unless a specific state law or employment contract protects them."
        ),
        LegalQuizQuestion(
            question = "What is the 'Plain View Doctrine'?",
            options = listOf(
                "A rule that allows you to see the evidence files before your court date.",
                "An exception allowing police to seize evidence without a warrant if it is immediately visible from a lawful vantage point.",
                "A law requiring courtrooms to be transparent to the public.",
                "A rule protecting open fields from drone surveillance."
            ),
            correctOptionIndex = 1,
            explanation = "If an officer is lawfully standing next to your car or outside your door and sees illegal drugs or weapons in plain view, they do not need a warrant to seize them. The items are considered exposed to the public."
        )
    )

    // --- Interactive Law Classes ---
    val lawClasses: List<LawClass> = listOf(
        LawClass(
            id = "class_civil_rights",
            title = "Civil Rights: Surviving a Police Traffic Stop",
            category = "Constitutional Law",
            videoUrl = "https://www.youtube.com/embed/S_8qA6F_M6Q", // educational representation
            summary = "Learn your rights when pulled over, how to handle requests for searches, and what to do if your rights are violated.",
            detailedText = "A traffic stop is legally classified as a temporary seizure under the Fourth Amendment. \n\n" +
                    "**1. What you MUST do:**\n" +
                    "- Pull over safely and turn on your dome light at night.\n" +
                    "- Present your driver's license, registration, and proof of insurance upon request.\n" +
                    "- Keep hands visible on the steering wheel.\n\n" +
                    "**2. What you do NOT have to do:**\n" +
                    "- You do NOT have to answer probing questions like 'Where are you coming from?' or 'Have you had anything to drink?'. You can reply: 'Officer, I prefer to keep my private details private.'\n" +
                    "- You do NOT have to consent to a search of your vehicle. Assert: 'I do not consent to any searches.'\n" +
                    "- Passengers also have rights: they do not have to provide identification unless the officer has specific, separate suspicion of them committing a crime (in some states).\n\n" +
                    "**3. Crucial Law Facts:**\n" +
                    "- Under *Rodriguez v. United States* (Supreme Court, 2015), an officer cannot prolong a traffic stop simply to wait for a drug-sniffing dog without separate reasonable suspicion. If they keep you longer than it takes to write the ticket, they are violating your 4th Amendment rights!",
            interactiveQuiz = listOf(
                LegalQuizQuestion(
                    question = "How long can a police officer keep you at a traffic stop?",
                    options = listOf(
                        "As long as they want to investigate.",
                        "Only the amount of time reasonably necessary to address the traffic infraction that caused the stop.",
                        "Strictly 15 minutes.",
                        "Until a supervisor arrives."
                    ),
                    correctOptionIndex = 1,
                    explanation = "The Supreme Court in Rodriguez v. US ruled that once the mission of the stop (writing a ticket or warning) is complete, the stop must end. Extending it to sniff the car or question you without independent probable cause is unconstitutional."
                )
            )
        ),
        LawClass(
            id = "class_tenant_defense",
            title = "Renters' Defense: Landlord Wars",
            category = "Civil & Housing Law",
            videoUrl = "https://www.youtube.com/embed/gL2-WkZk_bE",
            summary = "Master lease contracts, combat wrongful eviction, defeat illegal security deposit withholding, and force repairs.",
            detailedText = "Housing is a fundamental right, and landlords are bound by strict state codes.\n\n" +
                    "**1. Security Deposit Retention:**\n" +
                    "- In most states (e.g., CA, NY), landlords have a limited time (e.g., 14 to 21 days) to return your deposit or provide an itemized list of deductions with receipts. If they miss this deadline, they lose the legal right to keep ANY of your deposit and may face triple-damage penalties in small claims court!\n\n" +
                    "**2. Warranty of Habitability:**\n" +
                    "- Every residential lease has an implied 'Warranty of Habitability'. The landlord MUST provide running hot water, heating, working plumbing, secure locks, and a pest-free environment. If they fail to fix major issues, you have options like 'Repair and Deduct' or withholding rent, but you must follow exact written notice rules.\n\n" +
                    "**3. Constructive Eviction:**\n" +
                    "- If a landlord turns off your utilities, changes locks, or makes life unlivable, they have committed 'Self-Help Eviction' which is highly illegal. You can sue them for severe damages.",
            interactiveQuiz = listOf(
                LegalQuizQuestion(
                    question = "What happens if a landlord fails to return your security deposit or itemized receipts within the state's legal deadline?",
                    options = listOf(
                        "They can return it whenever they want as long as they apologize.",
                        "They forfeit the right to retain any portion of the deposit, and you can sue for up to treble (3x) damages.",
                        "They are allowed to keep 50% automatically.",
                        "The lease is voided immediately."
                    ),
                    correctOptionIndex = 1,
                    explanation = "State laws are very strict. If a landlord exceeds the statutory deadline (e.g., 21 days in CA), they forfeit all rights to withhold your deposit for damages. You are entitled to the full return, and bad-faith withholding can yield extra punitive damages in small claims court."
                )
            )
        )
    )
}
