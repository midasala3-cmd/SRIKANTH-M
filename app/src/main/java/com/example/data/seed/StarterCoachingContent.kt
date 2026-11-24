package com.example.data.seed

import com.example.data.model.CbtQuestion
import com.example.data.model.CoachingCourse
import com.example.data.model.ExamCategory
import com.example.data.model.MockTest
import com.example.data.model.StudyBlog

object StarterCoachingContent {

    val flagshipCourses: List<CoachingCourse> = listOf(
        CoachingCourse(
            id = "course_appsc_g2",
            examCategory = ExamCategory.APPSC_GROUP2,
            titleEn = "APPSC Group 2 Prelims + Mains Target Batch",
            titleTe = "APPSC గ్రూప్ 2 ప్రిలిమ్స్ + మెయిన్స్ టార్గెట్ బ్యాచ్",
            subtitleEn = "Complete AP History, Polity, Economy & S&T in English & Telugu",
            subtitleTe = "సంపూర్ణ ఏపీ చరిత్ర, పాలిటీ, ఎకానమీ & సైన్స్ అండ్ టెక్నాలజీ",
            durationLabelEn = "12 Months Access",
            durationLabelTe = "12 నెలల యాక్సెస్",
            mockTestsCount = 60,
            videoHours = 420,
            priceDisplay = "₹2,499",
            originalPriceDisplay = "₹5,999",
            highlightsEn = listOf(
                "AP History & Bifurcation Act deep dive",
                "60 CBT Mock Tests with -1/3 Negative Marking",
                "Bilingual PDF Notes (EN & తెలుగు)"
            ),
            highlightsTe = listOf(
                "ఏపీ చరిత్ర & పునర్విభజన చట్టం పూర్తి విశ్లేషణ",
                "-1/3 నెగటివ్ మార్కింగ్ తో 60 CBT మాక్ టెస్టులు",
                "ద్విభాషా PDF నోట్స్ (English & తెలుగు)"
            ),
            featuredBadgeEn = "Most Popular",
            featuredBadgeTe = "అత్యధిక ఆదరణ"
        ),
        CoachingCourse(
            id = "course_ap_police_si",
            examCategory = ExamCategory.AP_POLICE_SI,
            titleEn = "AP Police SI (SCT) Comprehensive Ranker",
            titleTe = "ఏపీ పోలీస్ ఎస్ఐ (SCT) సంపూర్ణ ర్యాంకర్ బ్యాచ్",
            subtitleEn = "Arithmetic, Reasoning, GS & Qualifying Papers + PET Physical Plan",
            subtitleTe = "అరిథ్మెటిక్, రీజనింగ్, జనరల్ స్టడీస్ & PET ఫిజికల్ ప్లాన్",
            durationLabelEn = "10 Months Access",
            durationLabelTe = "10 నెలల యాక్సెస్",
            mockTestsCount = 50,
            videoHours = 360,
            priceDisplay = "₹1,999",
            originalPriceDisplay = "₹4,499",
            highlightsEn = listOf(
                "200-Mark Arithmetic & Reasoning Shortcut Drills",
                "SLPRB Pattern Full-Length CBT Grand Tests",
                "1600m Run & Long Jump Weekly Training Chart"
            ),
            highlightsTe = listOf(
                "200 మార్కుల అరిథ్మెటిక్ & రీజనింగ్ షార్ట్‌కట్ ట్రిక్స్",
                "SLPRB ప్యాటర్న్ ఫుల్-లెంగ్త్ CBT గ్రాండ్ టెస్టులు",
                "1600 మీటర్ల రన్ & లాంగ్ జంప్ వారంవారీ ప్లాన్"
            ),
            featuredBadgeEn = "SLPRB Pattern",
            featuredBadgeTe = "SLPRB ప్యాటర్న్"
        ),
        CoachingCourse(
            id = "course_appsc_g1",
            examCategory = ExamCategory.APPSC_GROUP1,
            titleEn = "APPSC Group 1 Civil Services Foundation",
            titleTe = "APPSC గ్రూప్ 1 సివిల్ సర్వీసెస్ ఫౌండేషన్",
            subtitleEn = "Prelims Paper I & II + Mains Answer Writing Mentorship",
            subtitleTe = "ప్రిలిమ్స్ పేపర్ I & II + మెయిన్స్ ఆన్సర్ రైటింగ్ మెంటర్‌షిప్",
            durationLabelEn = "18 Months Access",
            durationLabelTe = "18 నెలల యాక్సెస్",
            mockTestsCount = 45,
            videoHours = 620,
            priceDisplay = "₹4,999",
            originalPriceDisplay = "₹11,999",
            highlightsEn = listOf(
                "AP Socio-Economic Survey & Budget Analysis",
                "Weekly Mains Descriptive Evaluation",
                "Current Affairs Monthly Magazines (EN & TE)"
            ),
            highlightsTe = listOf(
                "ఏపీ సామాజిక-ఆర్థిక సర్వే & బడ్జెట్ విశ్లేషణ",
                "వారంవారీ మెయిన్స్ డిస్క్రిప్టివ్ మూల్యాంకనం",
                "నెలవారీ కరెంట్ అఫైర్స్ మ్యాగజైన్స్ (EN & TE)"
            ),
            featuredBadgeEn = "Officer Track",
            featuredBadgeTe = "ఆఫీసర్ ట్రాక్"
        ),
        CoachingCourse(
            id = "course_ap_constable",
            examCategory = ExamCategory.AP_POLICE_CONSTABLE,
            titleEn = "AP Police Constable Prelims & Mains Express",
            titleTe = "ఏపీ పోలీస్ కానిస్టేబుల్ ప్రిలిమ్స్ & మెయిన్స్ ఎక్స్‌ప్రెస్",
            subtitleEn = "Fast-Track Bilingual Classes + 40 Full Mock Exams",
            subtitleTe = "ఫాస్ట్-ట్రాక్ ద్విభాషా తరగతులు + 40 ఫుల్ మాక్ పరీక్షలు",
            durationLabelEn = "8 Months Access",
            durationLabelTe = "8 నెలల యాక్సెస్",
            mockTestsCount = 40,
            videoHours = 240,
            priceDisplay = "₹1,299",
            originalPriceDisplay = "₹2,999",
            highlightsEn = listOf(
                "SCERT Class 6–10 Science & Social Capsules",
                "Previous 10 Years AP Police Solved Papers",
                "Topic-wise Speed Drills with Solutions"
            ),
            highlightsTe = listOf(
                "SCERT 6–10 తరగతుల సైన్స్ & సోషల్ క్యాప్సూల్స్",
                "గత 10 ఏళ్ల ఏపీ పోలీస్ సాల్వ్డ్ పేపర్లు",
                "వివరణాత్మక పరిష్కారాలతో టాపిక్-వైజ్ డ్రిల్స్"
            ),
            featuredBadgeEn = "Fast Track",
            featuredBadgeTe = "ఫాస్ట్ ట్రాక్"
        )
    )

    val starterMockTests: List<MockTest> = listOf(
        MockTest(
            id = "starter_appsc_g2_demo",
            authorId = "system_faculty",
            titleEn = "APPSC Group 2 Free Demo CBT Mock Test",
            titleTe = "APPSC గ్రూప్ 2 ఉచిత డెమో CBT మాక్ టెస్ట్",
            descriptionEn = "High-yield bilingual screening test covering AP History, Indian Polity, AP Economy & Mental Ability with -1/3 negative marking.",
            descriptionTe = "ఏపీ చరిత్ర, భారత రాజ్యాంగం, ఏపీ ఎకానమీ & మెంటల్ ఎబిలిటీపై -1/3 నెగటివ్ మార్కింగ్ తో కూడిన ద్విభాషా డెమో టెస్ట్.",
            examCategory = ExamCategory.APPSC_GROUP2.code,
            durationMinutes = 12,
            marksPerQuestion = 1.0,
            negativeMarking = 0.33,
            freeDemo = true,
            status = "published",
            bundledStarter = true,
            questions = listOf(
                CbtQuestion(
                    questionEn = "Which Satavahana ruler is credited with issuing bilingual coins featuring Prakrit and Dravidian (early Telugu/Tamil) legends and performing two Ashvamedha sacrifices?",
                    questionTe = "ప్రాకృతం మరియు ద్రావిడ లిపులతో కూడిన ద్విభాషా నాణేలను జారీ చేసి, రెండు అశ్వమేధ యాగాలు నిర్వహించిన శాతవాహన రాజు ఎవరు?",
                    optionsEn = listOf("Gautamiputra Satakarni", "Satakarni I", "Vasishthiputra Pulomavi", "Yajna Sri Satakarni"),
                    optionsTe = listOf("గౌతమీపుత్ర శాతకర్ణి", "మొదటి శాతకర్ణి", "వాసిష్ఠీపుత్ర పులోమావి", "యజ్ఞశ్రీ శాతకర్ణి"),
                    correctIndex = 1,
                    explanationEn = "Step 1: Recall the Nanaghat inscription composed by Queen Naganika.\nStep 2: It records that Satakarni I performed two Ashvamedha sacrifices and one Rajasuya sacrifice.\nStep 3: Therefore, Satakarni I is the correct answer.",
                    explanationTe = "దశ 1: రాణి నాగానిక వేయించిన నానాఘాట్ శాసనాన్ని గుర్తు చేసుకోండి.\nదశ 2: మొదటి శాతకర్ణి రెండు అశ్వమేధ యాగాలు మరియు ఒక రాజసూయ యాగం నిర్వహించినట్లు ఇందులో పేర్కొనబడింది.\nదశ 3: కావున సరైన సమాధానం మొదటి శాతకర్ణి.",
                    section = "AP History"
                ),
                CbtQuestion(
                    questionEn = "Under which Schedule of the Andhra Pradesh Reorganisation Act, 2014 are the state-level public sector corporations and companies listed for division between AP and Telangana?",
                    questionTe = "ఆంధ్రప్రదేశ్ పునర్విభజన చట్టం, 2014 లోని ఏ షెడ్యూల్‌లో ఆంధ్రప్రదేశ్ మరియు తెలంగాణ మధ్య విభజించాల్సిన ప్రభుత్వ రంగ సంస్థల జాబితా ఉంది?",
                    optionsEn = listOf("Schedule VII", "Schedule VIII", "Schedule IX", "Schedule X"),
                    optionsTe = listOf("షెడ్యూల్ VII", "షెడ్యూల్ VIII", "షెడ్యూల్ IX", "షెడ్యూల్ X"),
                    correctIndex = 2,
                    explanationEn = "Step 1: Schedule IX of the AP Reorganisation Act, 2014 enumerates 89 state government companies and corporations (e.g., APSRTC, APGENCO).\nStep 2: Schedule X covers training institutions (like APARD, Telugu Academy).\nStep 3: Hence, Schedule IX is the exact match.",
                    explanationTe = "దశ 1: ఏపీ పునర్విభజన చట్టం, 2014 లోని షెడ్యూల్ IX లో 89 ప్రభుత్వ రంగ సంస్థలు మరియు కార్పొరేషన్లు (ఉదా: APSRTC, APGENCO) ఉన్నాయి.\nదశ 2: షెడ్యూల్ X శిక్షణా సంస్థలకు సంబంధించినది.\nదశ 3: కావున షెడ్యూల్ IX సరైన సమాధానం.",
                    section = "AP Bifurcation"
                ),
                CbtQuestion(
                    questionEn = "Which Article of the Constitution of India empowers the President to make special provisions with respect to the State of Andhra Pradesh regarding equitable opportunities in public employment and education?",
                    questionTe = "ప్రభుత్వ ఉద్యోగాలు మరియు విద్యలో సమాన అవకాశాల కోసం ఆంధ్రప్రదేశ్ రాష్ట్రానికి ప్రత్యేక నిబంధనలు చేసే అధికారాన్ని రాష్ట్రపతికి కల్పించే భారత రాజ్యాంగ ఆర్టికల్ ఏది?",
                    optionsEn = listOf("Article 370", "Article 371-D", "Article 371-F", "Article 356"),
                    optionsTe = listOf("ఆర్టికల్ 370", "ఆర్టికల్ 371-D", "ఆర్టికల్ 371-F", "ఆర్టికల్ 356"),
                    correctIndex = 1,
                    explanationEn = "Step 1: The 32nd Constitutional Amendment Act (1973) inserted Articles 371-D and 371-E.\nStep 2: Article 371-D grants special provisions for equitable opportunities in public employment and education across regions of Andhra Pradesh.\nStep 3: Article 371-E provides for the establishment of a Central University in AP.",
                    explanationTe = "దశ 1: 1973 లోని 32వ రాజ్యాంగ సవరణ చట్టం ద్వారా ఆర్టికల్ 371-D మరియు 371-E చేర్చబడ్డాయి.\nదశ 2: ఆర్టికల్ 371-D ఆంధ్రప్రదేశ్‌లో ప్రభుత్వ ఉద్యోగాలు, విద్యలో సమాన అవకాశాలకు ప్రత్యేక రక్షణలు కల్పిస్తుంది.\nదశ 3: కావున ఆర్టికల్ 371-D సరైన సమాధానం.",
                    section = "Indian Polity"
                ),
                CbtQuestion(
                    questionEn = "The Polavaram Multipurpose Irrigation Project is constructed across which major river of Andhra Pradesh?",
                    questionTe = "పోలవరం బహుళార్థసాధక సాగునీటి ప్రాజెక్టు ఆంధ్రప్రదేశ్‌లోని ఏ ప్రధాన నదిపై నిర్మించబడింది?",
                    optionsEn = listOf("Krishna River", "Pennar River", "Godavari River", "Vamsadhara River"),
                    optionsTe = listOf("కృష్ణా నది", "పెన్నా నది", "గోదావరి నది", "వంశధార నది"),
                    correctIndex = 2,
                    explanationEn = "Step 1: Polavaram (Indira Sagar) Project is a National Project declared under Section 90 of the AP Reorganisation Act, 2014.\nStep 2: It is built across the Godavari River near Ramayyapeta in Eluru/Polavaram region.\nStep 3: It also facilitates Godavari-Krishna river interlinking.",
                    explanationTe = "దశ 1: పోలవరం ప్రాజెక్టు ఏపీ పునర్విభజన చట్టం సెక్షన్ 90 ప్రకారం జాతీయ ప్రాజెక్టుగా ప్రకటించబడింది.\nదశ 2: ఇది గోదావరి నదిపై నిర్మించబడుతోంది.\nదశ 3: ఇది గోదావరి-కృష్ణా నదుల అనుసంధానానికి కూడా తోడ్పడుతుంది.",
                    section = "AP Geography"
                ),
                CbtQuestion(
                    questionEn = "If a sum of ₹12,000 earns simple interest of ₹2,880 in 3 years, what is the annual rate of interest?",
                    questionTe = "₹12,000 అసలుపై 3 సంవత్సరాలకు ₹2,880 సాధారణ వడ్డీ లభిస్తే, వార్షిక వడ్డీ రేటు ఎంత?",
                    optionsEn = listOf("6%", "7.5%", "8%", "9%"),
                    optionsTe = listOf("6%", "7.5%", "8%", "9%"),
                    correctIndex = 2,
                    explanationEn = "Step 1: Use the formula SI = (P × T × R) / 100.\nStep 2: Substitute P = 12,000, T = 3, SI = 2,880 -> 2880 = (12000 × 3 × R) / 100.\nStep 3: 2880 = 360 × R -> R = 2880 / 360 = 8%.",
                    explanationTe = "దశ 1: సూత్రం SI = (P × T × R) / 100 ఉపయోగించండి.\nదశ 2: 2880 = (12000 × 3 × R) / 100.\nదశ 3: 2880 = 360 × R -> R = 2880 / 360 = 8%.",
                    section = "Mental Ability"
                ),
                CbtQuestion(
                    questionEn = "Who founded the 'Rajahmundry Social Reform Association' in 1878 to promote widow remarriage and women's education in Andhra?",
                    questionTe = "1878లో ఆంధ్రలో వితంతు పునర్వివాహం మరియు స్త్రీ విద్యను ప్రోత్సహించడానికి 'రాజమండ్రి సోషల్ రిఫార్మ్ అసోసియేషన్'ను స్థాపించినది ఎవరు?",
                    optionsEn = listOf("Gurajada Apparao", "Kandukuri Veeresalingam Pantulu", "Raghupati Venkataratnam Naidu", "Tanguturi Prakasam"),
                    optionsTe = listOf("గురజాడ అప్పారావు", "కందుకూరి వీరేశలింగం పంతులు", "రఘుపతి వెంకటరత్నం నాయుడు", "టంగుటూరి ప్రకాశం"),
                    correctIndex = 1,
                    explanationEn = "Step 1: Kandukuri Veeresalingam Pantulu is revered as the 'Father of the Telugu Renaissance'.\nStep 2: He founded the Rajahmundry Social Reform Association in 1878 and performed the first widow remarriage in Andhra on December 11, 1881.",
                    explanationTe = "దశ 1: కందుకూరి వీరేశలింగం పంతులు 'తెలుగు పునరుజ్జీవన పితామహుడు'గా ప్రసిద్ధి చెందారు.\nదశ 2: ఆయన 1878లో రాజమండ్రి సోషల్ రిఫార్మ్ అసోసియేషన్ స్థాపించి, 1881 డిసెంబర్ 11న ఆంధ్రలో మొదటి వితంతు వివాహం జరిపించారు.",
                    section = "AP History"
                )
            )
        ),
        MockTest(
            id = "starter_ap_police_si_demo",
            authorId = "system_faculty",
            titleEn = "AP Police SI Free Demo Grand Test",
            titleTe = "ఏపీ పోలీస్ ఎస్ఐ ఉచిత డెమో గ్రాండ్ టెస్ట్",
            descriptionEn = "SLPRB pattern bilingual mock exam covering Arithmetic, Logical Reasoning, General Science & Indian Constitution with -0.25 negative marking.",
            descriptionTe = "SLPRB ప్యాటర్న్ లో అరిథ్మెటిక్, రీజనింగ్, జనరల్ సైన్స్ & రాజ్యాంగంపై -0.25 నెగటివ్ మార్కింగ్ తో ద్విభాషా గ్రాండ్ టెస్ట్.",
            examCategory = ExamCategory.AP_POLICE_SI.code,
            durationMinutes = 10,
            marksPerQuestion = 1.0,
            negativeMarking = 0.25,
            freeDemo = true,
            status = "published",
            bundledStarter = true,
            questions = listOf(
                CbtQuestion(
                    questionEn = "A police patrol jeep covers 180 km in 3 hours. If its speed is increased by 15 km/h, how much time will it take to cover 300 km?",
                    questionTe = "ఒక పోలీస్ పెట్రోల్ జీప్ 3 గంటల్లో 180 కి.మీ. ప్రయాణిస్తుంది. దాని వేగాన్ని గంటకు 15 కి.మీ. పెంచితే, 300 కి.మీ. ప్రయాణించడానికి ఎంత సమయం పడుతుంది?",
                    optionsEn = listOf("3 hours 30 mins", "4 hours", "4 hours 30 mins", "5 hours"),
                    optionsTe = listOf("3 గంటల 30 నిమిషాలు", "4 గంటలు", "4 గంటల 30 నిమిషాలు", "5 గంటలు"),
                    correctIndex = 1,
                    explanationEn = "Step 1: Original speed = Distance / Time = 180 / 3 = 60 km/h.\nStep 2: New speed = 60 + 15 = 75 km/h.\nStep 3: Time for 300 km = 300 / 75 = 4 hours.",
                    explanationTe = "దశ 1: అసలు వేగం = 180 / 3 = 60 కి.మీ./గం.\nదశ 2: కొత్త వేగం = 60 + 15 = 75 కి.మీ./గం.\nదశ 3: 300 కి.మీ. కు పట్టే సమయం = 300 / 75 = 4 గంటలు.",
                    section = "Arithmetic"
                ),
                CbtQuestion(
                    questionEn = "In a certain code language, 'POLICE' is written as 'QPMJDF'. How will 'CONSTABLE' be written in the same code?",
                    questionTe = "ఒక కోడ్ భాషలో 'POLICE' ను 'QPMJDF' అని రాస్తే, అదే కోడ్‌లో 'CONSTABLE' ను ఎలా రాస్తారు?",
                    optionsEn = listOf("DPOTUBCMF", "DPOTUBCME", "CPOTUBCMF", "DPOSUBCMF"),
                    optionsTe = listOf("DPOTUBCMF", "DPOTUBCME", "CPOTUBCMF", "DPOSUBCMF"),
                    correctIndex = 0,
                    explanationEn = "Step 1: Compare each letter of POLICE -> QPMJDF: every letter is shifted forward by +1 (P->Q, O->P, L->M, I->J, C->D, E->F).\nStep 2: Apply +1 to CONSTABLE: C->D, O->P, N->O, S->T, T->U, A->B, B->C, L->M, E->F.\nStep 3: Result is DPOTUBCMF.",
                    explanationTe = "దశ 1: POLICE లోని ప్రతి అక్షరానికి +1 కలిపితే QPMJDF వస్తుంది.\nదశ 2: CONSTABLE లోని ప్రతి అక్షరానికి +1 కలపగా DPOTUBCMF వస్తుంది.",
                    section = "Reasoning"
                ),
                CbtQuestion(
                    questionEn = "Which writ literally means 'to have the body of' and is issued by the Supreme Court or High Court against illegal detention?",
                    questionTe = "అక్రమ నిర్బంధానికి వ్యతిరేకంగా సుప్రీంకోర్టు లేదా హైకోర్టు జారీ చేసే 'శరీరాన్ని హాజరుపరచండి' అనే అర్థం గల రిట్ ఏది?",
                    optionsEn = listOf("Mandamus", "Certiorari", "Habeas Corpus", "Quo Warranto"),
                    optionsTe = listOf("మాండమస్", "సెర్షియోరరీ", "హెబియస్ కార్పస్", "కో-వారెంటో"),
                    correctIndex = 2,
                    explanationEn = "Step 1: Under Articles 32 and 226, courts issue five writs.\nStep 2: 'Habeas Corpus' (Latin: to have the body) protects individual liberty against unlawful detention by public or private authorities.",
                    explanationTe = "దశ 1: ఆర్టికల్ 32 మరియు 226 కింద న్యాయస్థానాలు ఐదు రకాల రిట్లను జారీ చేస్తాయి.\nదశ 2: 'హెబియస్ కార్పస్' వ్యక్తిగత స్వేచ్ఛను కాపాడుతూ అక్రమ నిర్బంధం నుండి రక్షణ కల్పిస్తుంది.",
                    section = "Indian Polity"
                ),
                CbtQuestion(
                    questionEn = "Which vitamin deficiency causes 'Scurvy', characterized by bleeding gums and delayed wound healing?",
                    questionTe = "చిగుళ్ల నుండి రక్తస్రావం మరియు గాయాలు ఆలస్యంగా మానడం వంటి లక్షణాలు గల 'స్కర్వీ' వ్యాధి ఏ విటమిన్ లోపం వల్ల వస్తుంది?",
                    optionsEn = listOf("Vitamin A", "Vitamin B12", "Vitamin C", "Vitamin D"),
                    optionsTe = listOf("విటమిన్ A", "విటమిన్ B12", "విటమిన్ C", "విటమిన్ D"),
                    correctIndex = 2,
                    explanationEn = "Step 1: Vitamin C (Ascorbic Acid) is essential for collagen synthesis.\nStep 2: Its deficiency causes Scurvy (bleeding gums).\nStep 3: Citrus fruits like amla, lemon, and orange are rich sources.",
                    explanationTe = "దశ 1: విటమిన్ C (ఆస్కార్బిక్ ఆమ్లం) లోపం వల్ల స్కర్వీ వ్యాధి వస్తుంది.\nదశ 2: ఉసిరి, నిమ్మ, నారింజ వంటి సిట్రస్ పండ్లలో విటమిన్ C పుష్కలంగా ఉంటుంది.",
                    section = "General Science"
                ),
                CbtQuestion(
                    questionEn = "Find the next number in the series: 7, 14, 28, 56, 112, ?",
                    questionTe = "ఈ సంఖ్యా శ్రేణిలో తదుపరి సంఖ్యను కనుగొనండి: 7, 14, 28, 56, 112, ?",
                    optionsEn = listOf("168", "212", "224", "240"),
                    optionsTe = listOf("168", "212", "224", "240"),
                    correctIndex = 2,
                    explanationEn = "Step 1: Observe the ratio between consecutive terms: 14/7 = 2, 28/14 = 2, 56/28 = 2.\nStep 2: Multiply the last term by 2: 112 × 2 = 224.",
                    explanationTe = "దశ 1: ప్రతి సంఖ్యను 2 తో గుణిస్తే తదుపరి సంఖ్య వస్తుంది (7×2=14, 14×2=28...).\nదశ 2: 112 × 2 = 224.",
                    section = "Reasoning"
                )
            )
        ),
        MockTest(
            id = "starter_ap_constable_demo",
            authorId = "system_faculty",
            titleEn = "AP Police Constable Quick Speed Test",
            titleTe = "ఏపీ పోలీస్ కానిస్టేబుల్ స్పీడ్ మాక్ టెస్ట్",
            descriptionEn = "SCERT Science, AP Geography & Basic Arithmetic drill designed for AP Police Constable Prelims.",
            descriptionTe = "ఏపీ పోలీస్ కానిస్టేబుల్ ప్రిలిమ్స్ కోసం SCERT సైన్స్, ఏపీ జాగ్రఫీ & ప్రాథమిక అరిథ్మెటిక్ ప్రాక్టీస్ టెస్ట్.",
            examCategory = ExamCategory.AP_POLICE_CONSTABLE.code,
            durationMinutes = 8,
            marksPerQuestion = 1.0,
            negativeMarking = 0.25,
            freeDemo = true,
            status = "published",
            bundledStarter = true,
            questions = listOf(
                CbtQuestion(
                    questionEn = "Which wildlife sanctuary in Andhra Pradesh is famous for the endangered Great Indian Bustard?",
                    questionTe = "అంతరించిపోతున్న 'గ్రేట్ ఇండియన్ బస్టర్డ్' పక్షికి ప్రసిద్ధి చెందిన ఆంధ్రప్రదేశ్ వన్యప్రాణి అభయారణ్యం ఏది?",
                    optionsEn = listOf("Coringa Wildlife Sanctuary", "Rollapadu Wildlife Sanctuary", "Koundinya Wildlife Sanctuary", "Papikonda National Park"),
                    optionsTe = listOf("కోరింగ వన్యప్రాణి అభయారణ్యం", "రోళ్లపాడు వన్యప్రాణి అభయారణ్యం", "కౌండిన్య వన్యప్రాణి అభయారణ్యం", "పాపికొండల జాతీయ పార్కు"),
                    correctIndex = 1,
                    explanationEn = "Step 1: Rollapadu Wildlife Sanctuary is located in Nandyal (erstwhile Kurnool) district of Andhra Pradesh.\nStep 2: It is the prime grassland habitat protecting the critically endangered Great Indian Bustard and Lesser Florican.",
                    explanationTe = "దశ 1: రోళ్లపాడు వన్యప్రాణి అభయారణ్యం ఆంధ్రప్రదేశ్‌లోని నంద్యాల (ఉమ్మడి కర్నూలు) జిల్లాలో ఉంది.\nదశ 2: ఇది అంతరించిపోతున్న గ్రేట్ ఇండియన్ బస్టర్డ్ పక్షి సంరక్షణకు ప్రసిద్ధి చెందింది.",
                    section = "AP Geography"
                ),
                CbtQuestion(
                    questionEn = "What is the SI unit of electric current?",
                    questionTe = "విద్యుత్ ప్రవాహానికి (Electric Current) అంతర్జాతీయ ప్రమాణం (SI Unit) ఏది?",
                    optionsEn = listOf("Volt", "Ohm", "Ampere", "Watt"),
                    optionsTe = listOf("వోల్ట్", "ఓమ్", "ఆంపియర్", "వాట్"),
                    correctIndex = 2,
                    explanationEn = "Step 1: Electric current is the rate of flow of electric charge (I = Q/t).\nStep 2: Its SI unit is Ampere (A). Volt is potential difference, Ohm is resistance, and Watt is power.",
                    explanationTe = "దశ 1: విద్యుత్ ప్రవాహానికి SI ప్రమాణం ఆంపియర్ (Ampere).\nదశ 2: వోల్ట్ పొటెన్షియల్ తేడాకు, ఓమ్ నిరోధానికి, వాట్ సామర్థ్యానికి ప్రమాణాలు.",
                    section = "General Science"
                ),
                CbtQuestion(
                    questionEn = "What is 25% of 480?",
                    questionTe = "480 లో 25% విలువ ఎంత?",
                    optionsEn = listOf("96", "110", "120", "140"),
                    optionsTe = listOf("96", "110", "120", "140"),
                    correctIndex = 2,
                    explanationEn = "Step 1: 25% is equivalent to the fraction 1/4.\nStep 2: 480 × (1/4) = 120.",
                    explanationTe = "దశ 1: 25% అంటే 1/4 వంతు.\nదశ 2: 480 / 4 = 120.",
                    section = "Arithmetic"
                ),
                CbtQuestion(
                    questionEn = "Who is known as 'Andhra Kesari' (Lion of Andhra) and served as the first Chief Minister of Andhra State in 1953?",
                    questionTe = "'ఆంధ్ర కేసరి'గా ప్రసిద్ధి చెంది, 1953లో ఆంధ్ర రాష్ట్ర మొదటి ముఖ్యమంత్రిగా పనిచేసిన నాయకుడు ఎవరు?",
                    optionsEn = listOf("Potti Sriramulu", "Tanguturi Prakasam Pantulu", "Neelam Sanjiva Reddy", "Alluri Sitarama Raju"),
                    optionsTe = listOf("పొట్టి శ్రీరాములు", "టంగుటూరి ప్రకాశం పంతులు", "నీలం సంజీవరెడ్డి", "అల్లూరి సీతారామరాజు"),
                    correctIndex = 1,
                    explanationEn = "Step 1: Tanguturi Prakasam Pantulu earned the title 'Andhra Kesari' during the 1928 Simon Commission protest in Madras.\nStep 2: He became the first Chief Minister of Andhra State on October 1, 1953, with Kurnool as capital.",
                    explanationTe = "దశ 1: 1928 సైమన్ కమిషన్ వ్యతిరేక ఉద్యమంలో ధైర్యసాహసాలకు టంగుటూరి ప్రకాశం పంతులు 'ఆంధ్ర కేసరి' బిరుదు పొందారు.\nదశ 2: 1953 అక్టోబర్ 1న కర్నూలు రాజధానిగా ఏర్పడిన ఆంధ్ర రాష్ట్ర తొలి ముఖ్యమంత్రి అయ్యారు.",
                    section = "AP History"
                )
            )
        )
    )

    val starterBlogs: List<StudyBlog> = listOf(
        StudyBlog(
            id = "starter_blog_appsc_g2_plan",
            authorId = "system_faculty",
            titleEn = "APPSC Group 2 2026: Complete 90-Day Bilingual Study Roadmap & Negative Marking Strategy",
            titleTe = "APPSC గ్రూప్ 2 2026: 90 రోజుల సంపూర్ణ ప్రిపరేషన్ ప్రణాళిక & నెగటివ్ మార్కింగ్ వ్యూహం",
            slug = "appsc-group-2-2026-90-day-study-roadmap",
            metaDescription = "Master APPSC Group 2 Prelims & Mains with our 90-day bilingual English & Telugu timetable, AP History high-weightage topics, and -1/3 negative marking tips.",
            seoKeywords = listOf("appsc group 2", "ap history notes", "telugu coaching", "negative marking tips"),
            category = "APPSC",
            summaryEn = "Learn how top rankers balance AP History, Indian Polity, AP Economy, and daily CBT mock tests to clear the Group 2 cutoff comfortably.",
            summaryTe = "ఏపీ చరిత్ర, భారత పాలిటీ, ఏపీ ఎకానమీ మరియు డైలీ CBT మాక్ టెస్టులతో గ్రూప్ 2 కటాఫ్ సులభంగా దాటే వ్యూహాన్ని తెలుసుకోండి.",
            contentEn = "Clearing APPSC Group 2 requires precision under the 1/3rd negative marking scheme.\n\n1. Phase 1 (Days 1–35): Build Core Foundations\nFocus on Satavahanas, Ikshvakus, Eastern Chalukyas, Kakatiyas, and the Andhra Movement alongside Indian Constitution Articles 1–395. Read Telugu Akademi textbooks side by side with bilingual short notes.\n\n2. Phase 2 (Days 36–65): AP Economy & Science-Tech\nAnalyze the latest Andhra Pradesh Socio-Economic Survey, welfare indicators, industrial corridors, and ISRO/DRDO recent launches. Practice 30 MCQs daily.\n\n3. Phase 3 (Days 66–90): Full-Length CBT Drills\nAttempt at least 15 full-length Computer Based Tests on Vijeta AP. Never guess blindly when 0 options are eliminated—if you eliminate 2 options, your expected score becomes positive even with -0.33 penalty!",
            contentTe = "APPSC గ్రూప్ 2 పరీక్షలో 1/3 నెగటివ్ మార్కింగ్ విధానంలో విజయం సాధించడానికి ఖచ్చితమైన వ్యూహం అవసరం.\n\n1. మొదటి దశ (1–35 రోజులు): ప్రాథమిక అంశాలపై పట్టు\nశాతవాహనులు, ఇక్ష్వాకులు, తూర్పు చాళుక్యులు, కాకతీయులు మరియు ఆంధ్ర ఉద్యమంతో పాటు భారత రాజ్యాంగ ఆర్టికల్స్‌ను తెలుగు అకాడమీ పుస్తకాల నుండి క్షుణ్ణంగా చదవండి.\n\n2. రెండవ దశ (36–65 రోజులు): ఏపీ ఎకానమీ & సైన్స్ అండ్ టెక్నాలజీ\nఆంధ్రప్రదేశ్ సామాజిక-ఆర్థిక సర్వే, రాష్ట్ర బడ్జెట్, పారిశ్రామిక కారిడార్లు మరియు ఇస్రో ప్రయోగాలపై రోజుకు కనీసం 30 ప్రశ్నలు సాధన చేయండి.\n\n3. మూడవ దశ (66–90 రోజులు): CBT మాక్ టెస్టులు\nవిజేత AP లో కనీసం 15 ఫుల్-లెంగ్త్ CBT గ్రాండ్ టెస్టులు రాయండి. రెండు ఆప్షన్లను ఎలిమినేట్ చేయగలిగితేనే రిస్క్ తీసుకోండి, తద్వారా నెగటివ్ మార్కులను నివారించవచ్చు.",
            readTimeMinutes = 6,
            status = "published",
            bundledStarter = true
        ),
        StudyBlog(
            id = "starter_blog_ap_police_si_pet",
            authorId = "system_faculty",
            titleEn = "AP Police SI & Constable 2026: Written Exam Pattern + PET 1600m Running Guide",
            titleTe = "ఏపీ పోలీస్ ఎస్ఐ & కానిస్టేబుల్ 2026: రాత పరీక్ష ప్యాటర్న్ + PET 1600మీ రన్నింగ్ గైడ్",
            slug = "ap-police-si-constable-exam-pattern-pet-guide",
            metaDescription = "Complete SLPRB AP Police SI & Constable syllabus, Arithmetic 200-mark strategy, and step-by-step Physical Efficiency Test (PET) schedule.",
            seoKeywords = listOf("ap police si", "ap constable", "slprb syllabus", "pet 1600m"),
            category = "AP_POLICE",
            summaryEn = "Score 150+ in Arithmetic & Reasoning while building 1600m endurance and Long Jump power with our integrated daily routine.",
            summaryTe = "అరిథ్మెటిక్ & రీజనింగ్‌లో 150+ స్కోరు చేస్తూనే, 1600మీ పరుగు మరియు లాంగ్ జంప్‌లో మెరిట్ సాధించే రోజువారీ ప్రణాళిక.",
            contentEn = "Selection in AP SLPRB SI and Constable recruitments depends on balancing both the Objective CBT Written Exam and Physical Efficiency Test (PET).\n\n1. Arithmetic & Reasoning Weightage\nIn SI Mains Paper III (200 marks), mastering Ratios, Percentages, Time & Work, Time-Speed-Distance, and Syllogisms guarantees a decisive lead over the competition.\n\n2. General Studies & AP Current Affairs\nCover SCERT Class 6 to 10 Science and Social Studies thoroughly, plus the last 12 months of Andhra Pradesh state schemes and national awards.\n\n3. Morning PET Conditioning\nTrain 5 days a week: alternate 1600m tempo runs, 100m sprint starts, and plyometric squats for Long Jump. Keep Sunday for full recovery and a 2-hour CBT mock test.",
            contentTe = "ఏపీ SLPRB ఎస్ఐ మరియు కానిస్టేబుల్ నియామకాలలో రాత పరీక్ష మరియు ఫిజికల్ ఎఫిషియెన్సీ టెస్ట్ (PET) రెండింటినీ సమన్వయం చేసుకోవడం అత్యంత ముఖ్యం.\n\n1. అరిథ్మెటిక్ & రీజనింగ్ ప్రాధాన్యత\nఎస్ఐ మెయిన్స్ పేపర్ III (200 మార్కులు) లో నిష్పత్తి, శాతాలు, కాలం-పని, వేగం-దూరం మరియు సిలాజిజమ్స్ పై షార్ట్‌కట్ పద్ధతులు నేర్చుకుంటే అద్భుతమైన ర్యాంక్ వస్తుంది.\n\n2. జనరల్ స్టడీస్ & SCERT పుస్తకాలు\n6 నుండి 10వ తరగతి SCERT సైన్స్, సోషల్ పుస్తకాలు మరియు గత 12 నెలల ఏపీ కరెంట్ అఫైర్స్ తప్పనిసరిగా రివిజన్ చేయండి.\n\n3. ఉదయం PET శిక్షణ\nవారానికి 5 రోజులు 1600మీ పరుగు, 100మీ స్ప్రింట్స్ మరియు లాంగ్ జంప్ వ్యాయామాలు సాధన చేయండి.",
            readTimeMinutes = 5,
            status = "published",
            bundledStarter = true
        ),
        StudyBlog(
            id = "starter_blog_ap_bifurcation_digest",
            authorId = "system_faculty",
            titleEn = "AP Reorganisation Act 2014: Top 25 Must-Know Sections & Schedules for APPSC",
            titleTe = "ఆంధ్రప్రదేశ్ పునర్విభజన చట్టం 2014: APPSC కోసం తప్పనిసరిగా చదవాల్సిన 25 సెక్షన్లు & షెడ్యూల్స్",
            slug = "ap-reorganisation-act-2014-top-sections-schedules",
            metaDescription = "Quick bilingual revision notes on AP Reorganisation Act 2014 Parts I-XII, Schedule IX, Schedule X, Polavaram Section 90, and Schedule XIII.",
            seoKeywords = listOf("ap bifurcation act", "schedule ix x", "polavaram section 90", "appsc notes"),
            category = "CURRENT_AFFAIRS",
            summaryEn = "A crisp exam-ready bilingual reference sheet covering all 12 Parts and 13 Schedules of the AP Reorganisation Act, 2014.",
            summaryTe = "ఏపీ పునర్విభజన చట్టంలోని 12 భాగాలు మరియు 13 షెడ్యూల్స్ పై పరీక్షకు ఉపయోగపడే సంక్షిప్త ద్విభాషా నోట్స్.",
            contentEn = "Every APPSC Group 1 and Group 2 exam features 8 to 15 direct questions from the Andhra Pradesh Reorganisation Act, 2014.\n\nKey High-Yield Milestones:\n• Presidential Assent: March 1, 2014 | Appointed Day: June 2, 2014.\n• Total Structure: 12 Parts, 108 Sections, and 13 Schedules.\n• Section 8: Special responsibility of Governor for common capital law & order.\n• Section 85: Constitution of Krishna & Godavari River Management Boards (KRMB & GRMB).\n• Section 90: Declares Polavaram Irrigation Project as a National Project.\n• Schedule IX: List of 89 Government Companies & Corporations.\n• Schedule X: Continuation of facilities in 107 State Institutions.\n• Schedule XIII: Education & Infrastructure commitments (IIT Tirupati, IIM Visakhapatnam, AIIMS Mangalagiri, NIT Tadepalligudem).",
            contentTe = "ప్రతి APPSC గ్రూప్ 1 మరియు గ్రూప్ 2 పరీక్షలో ఏపీ పునర్విభజన చట్టం, 2014 నుండి 8 నుండి 15 ప్రశ్నలు నేరుగా వస్తాయి.\n\nముఖ్యమైన అంశాలు:\n• రాష్ట్రపతి ఆమోదం: మార్చి 1, 2014 | అపాయింటెడ్ డే: జూన్ 2, 2014.\n• మొత్తం నిర్మాణం: 12 భాగాలు, 108 సెక్షన్లు, 13 షెడ్యూల్స్.\n• సెక్షన్ 85: కృష్ణా మరియు గోదావరి నదీ యాజమాన్య బోర్డుల (KRMB & GRMB) ఏర్పాటు.\n• సెక్షన్ 90: పోలవరం సాగునీటి ప్రాజెక్టును జాతీయ ప్రాజెక్టుగా ప్రకటన.\n• షెడ్యూల్ IX: 89 ప్రభుత్వ రంగ సంస్థలు మరియు కార్పొరేషన్లు.\n• షెడ్యూల్ X: 107 రాష్ట్ర శిక్షణా సంస్థలలో సేవల కొనసాగింపు.\n• షెడ్యూల్ XIII: విద్యా & మౌలిక సదుపాయాల సంస్థలు (IIT తిరుపతి, IIM విశాఖపట్నం, AIIMS మంగళగిరి).",
            readTimeMinutes = 4,
            status = "published",
            bundledStarter = true
        )
    )
}
