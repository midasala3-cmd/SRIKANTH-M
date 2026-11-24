const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";
const ADMIN_UID = "admin_789";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

function pastDate(msAgo = 2000) {
  return new Date(Date.now() - msAgo);
}

function validUserProfile(uid, ts = pastDate()) {
  return {
    userId: uid,
    displayName: "Ravi Kumar",
    preferredLanguage: "EN",
    targetExam: "APPSC_GROUP2",
    enrolledCourseIds: ["course_appsc_g2"],
    bookmarkedBlogIds: [],
    createdAt: ts,
    updatedAt: ts,
  };
}

function validAttempt(uid, ts = pastDate()) {
  return {
    userId: uid,
    testId: "demo_appsc_g2_1",
    testTitleEn: "APPSC Group 2 Prelims Demo Mock",
    testTitleTe: "APPSC గ్రూప్ 2 ప్రిలిమ్స్ డెమో మాక్ టెస్ట్",
    examCategory: "APPSC_GROUP2",
    totalQuestions: 10,
    correctCount: 7,
    wrongCount: 2,
    unansweredCount: 1,
    score: 6.34,
    maxScore: 10.0,
    negativeMarking: 0.33,
    timeTakenSeconds: 420,
    accuracyPercent: 77.78,
    createdAt: ts,
  };
}

function validMockTest(authorId, status = "published", ts = pastDate()) {
  return {
    authorId,
    titleEn: "AP Police SI Grand Test 1",
    titleTe: "ఏపీ పోలీస్ ఎస్ఐ గ్రాండ్ టెస్ట్ 1",
    descriptionEn: "Bilingual mock test with negative marking.",
    descriptionTe: "నెగటివ్ మార్కింగ్ తో ద్విభాషా మాక్ టెస్ట్.",
    examCategory: "AP_POLICE_SI",
    durationMinutes: 30,
    marksPerQuestion: 1.0,
    negativeMarking: 0.25,
    freeDemo: true,
    status,
    questions: [
      {
        questionEn: "Which Article of the Indian Constitution deals with Equality before Law?",
        questionTe: "భారత రాజ్యాంగంలోని ఏ ఆర్టికల్ చట్టం ముందు సమానత్వాన్ని తెలుపుతుంది?",
        optionsEn: ["Article 14", "Article 19", "Article 21", "Article 32"],
        optionsTe: ["ఆర్టికల్ 14", "ఆర్టికల్ 19", "ఆర్టికల్ 21", "ఆర్టికల్ 32"],
        correctIndex: 0,
        explanationEn: "Article 14 guarantees equality before law and equal protection of laws.",
        explanationTe: "ఆర్టికల్ 14 చట్టం ముందు సమానత్వం మరియు చట్టాల సమాన రక్షణను కల్పిస్తుంది.",
        section: "Indian Polity",
      },
    ],
    createdAt: ts,
    updatedAt: ts,
  };
}

function validStudyBlog(authorId, status = "published", ts = pastDate()) {
  return {
    authorId,
    titleEn: "APPSC Group 2 2026 Preparation Strategy",
    titleTe: "APPSC గ్రూప్ 2 2026 ప్రిపరేషన్ వ్యూహం",
    slug: "appsc-group-2-2026-strategy",
    metaDescription: "Complete bilingual syllabus breakdown and daily timetable for APPSC Group 2 aspirants.",
    seoKeywords: ["appsc group 2", "ap history", "telugu notes"],
    category: "APPSC",
    summaryEn: "Master AP History, Polity, and Economy with a 90-day structured timetable.",
    summaryTe: "90 రోజుల ప్రణాళికతో ఏపీ చరిత్ర, పాలిటీ మరియు ఎకానమీ పై పట్టు సాధించండి.",
    contentEn: "Focus on Andhra Pradesh Reorganization Act, Satavahanas, Indian Polity articles, and daily mock tests with negative marking analysis.",
    contentTe: "ఆంధ్రప్రదేశ్ పునర్విభజన చట్టం, శాతవాహనులు, భారత రాజ్యాంగ ఆర్టికల్స్ మరియు నెగటివ్ మార్కింగ్ మాక్ టెస్టులపై దృష్టి పెట్టండి.",
    readTimeMinutes: 6,
    status,
    createdAt: ts,
    updatedAt: ts,
  };
}

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated users cannot read or write any collection", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(unauthDb.collection("mock_tests").where("status", "==", "published").get());
  await assertFails(unauthDb.collection("study_blogs").where("status", "==", "published").get());
  await assertFails(unauthDb.collection("mock_tests").doc("t1").set(validMockTest(ALICE_UID)));
});

test("User profile: owner create/update succeeds; cross-user & ghost fields fail", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();

  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).set(validUserProfile(ALICE_UID)));
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).get());

  // Partial update preserves immutable createdAt & userId
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).update({
      preferredLanguage: "TE",
      updatedAt: pastDate(500),
    })
  );

  // Reject ghost field / role escalation
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      isAdmin: true,
      updatedAt: pastDate(500),
    })
  );

  // Reject future timestamp
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      preferredLanguage: "EN",
      updatedAt: new Date(Date.now() + 60000),
    })
  );

  // Cross-user read & write rejected
  await assertFails(bobDb.collection("users").doc(ALICE_UID).get());
  await assertFails(bobDb.collection("users").doc(ALICE_UID).update({ displayName: "Hijacked", updatedAt: pastDate(500) }));
});

test("User exam attempts subcollection: owner create/list succeeds; update & cross-user fail", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("attempts").doc("att_1").set(validAttempt(ALICE_UID))
  );
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).collection("attempts").get());

  // Attempts are immutable after submission
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("attempts").doc("att_1").update({ score: 10.0 })
  );

  // Cross-user access denied
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("attempts").get());
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("attempts").doc("att_2").set(validAttempt(ALICE_UID))
  );
});

test("Mock tests: published query succeeds; unfiltered query, spoofing & cross-user edits fail", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const adminDb = testEnv.authenticatedContext(ADMIN_UID, {
    email: "midasala3@gmail.com",
    email_verified: true,
  }).firestore();
  const spoofedAdminDb = testEnv.authenticatedContext("spoof_uid", {
    email: "midasala3@gmail.com",
    email_verified: false,
  }).firestore();

  await assertSucceeds(aliceDb.collection("mock_tests").doc("test_1").set(validMockTest(ALICE_UID, "published")));

  // Reject identity spoofing on create
  await assertFails(bobDb.collection("mock_tests").doc("test_spoof").set(validMockTest(ALICE_UID, "published")));

  // Reject @DocumentId 'id' field in payload
  await assertFails(
    aliceDb.collection("mock_tests").doc("test_with_id").set({
      ...validMockTest(ALICE_UID, "published"),
      id: "test_with_id",
    })
  );

  // Filtered query matches rule
  await assertSucceeds(bobDb.collection("mock_tests").where("status", "==", "published").get());
  // Unfiltered list query rejected
  await assertFails(bobDb.collection("mock_tests").get());

  // Bob cannot modify or delete Alice's test
  await assertFails(bobDb.collection("mock_tests").doc("test_1").update({ titleEn: "Hacked", updatedAt: pastDate(500) }));
  await assertFails(bobDb.collection("mock_tests").doc("test_1").delete());

  // Unverified email spoofing admin fails
  await assertFails(spoofedAdminDb.collection("mock_tests").doc("test_1").delete());

  // Verified bootstrapped admin can update/delete
  await assertSucceeds(
    adminDb.collection("mock_tests").doc("test_1").update({
      titleEn: "AP Police SI Grand Test 1 (Verified)",
      updatedAt: pastDate(500),
    })
  );
  await assertSucceeds(adminDb.collection("mock_tests").doc("test_1").delete());
});

test("Study blogs: SEO validation, published list query, and ownership checks", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();

  await assertSucceeds(aliceDb.collection("study_blogs").doc("blog_1").set(validStudyBlog(ALICE_UID, "published")));

  // Invalid slug (spaces/uppercase) rejected
  await assertFails(
    aliceDb.collection("study_blogs").doc("blog_bad_slug").set({
      ...validStudyBlog(ALICE_UID, "published"),
      slug: "Bad Slug With Spaces!",
    })
  );

  // Filtered query succeeds; unfiltered query fails
  await assertSucceeds(bobDb.collection("study_blogs").where("status", "==", "published").get());
  await assertFails(bobDb.collection("study_blogs").get());

  // Owner partial update succeeds; cross-user delete fails
  await assertSucceeds(
    aliceDb.collection("study_blogs").doc("blog_1").update({
      readTimeMinutes: 8,
      updatedAt: pastDate(500),
    })
  );
  await assertFails(bobDb.collection("study_blogs").doc("blog_1").delete());
});

test("MockExamRepository: structured scoringRules create, partial update, author query & bounds enforcement", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const ts = pastDate();

  const structuredExam = {
    ...validMockTest(ALICE_UID, "draft", ts),
    marksPerQuestion: 1.0,
    negativeMarking: 0.33,
    scoringRules: {
      marksPerCorrect: 1.0,
      negativeMarkPerWrong: 0.33,
      unansweredPenalty: 0.0,
      negativeMarkingEnabled: true,
      passingPercentage: 35.0,
    },
    questions: [
      {
        orderIndex: 0,
        section: "Indian Polity",
        questionEn: "Which Article deals with Equality before Law?",
        questionTe: "చట్టం ముందు సమానత్వాన్ని తెలిపే ఆర్టికల్ ఏది?",
        options: [
          { key: "A", textEn: "Article 14", textTe: "ఆర్టికల్ 14" },
          { key: "B", textEn: "Article 19", textTe: "ఆర్టికల్ 19" },
          { key: "C", textEn: "Article 21", textTe: "ఆర్టికల్ 21" },
          { key: "D", textEn: "Article 32", textTe: "ఆర్టికల్ 32" },
        ],
        optionsEn: ["Article 14", "Article 19", "Article 21", "Article 32"],
        optionsTe: ["ఆర్టికల్ 14", "ఆర్టికల్ 19", "ఆర్టికల్ 21", "ఆర్టికల్ 32"],
        correctOptionIndex: 0,
        correctIndex: 0,
        explanationEn: "Article 14 guarantees equality before law.",
        explanationTe: "ఆర్టికల్ 14 చట్టం ముందు సమానత్వాన్ని కల్పిస్తుంది.",
        difficulty: "MEDIUM",
      },
    ],
  };

  // Author can create draft with full scoringRules
  await assertSucceeds(aliceDb.collection("mock_tests").doc("exam_rules_1").set(structuredExam));

  // Author-scoped list query matches rule
  await assertSucceeds(aliceDb.collection("mock_tests").where("authorId", "==", ALICE_UID).get());

  // Targeted partial update of scoringRules succeeds
  await assertSucceeds(
    aliceDb.collection("mock_tests").doc("exam_rules_1").update({
      marksPerQuestion: 2.0,
      negativeMarking: 0.5,
      scoringRules: {
        marksPerCorrect: 2.0,
        negativeMarkPerWrong: 0.5,
        unansweredPenalty: 0.0,
        negativeMarkingEnabled: true,
        passingPercentage: 40.0,
      },
      updatedAt: pastDate(500),
    })
  );

  // Invalid scoringRules (negativeMarkPerWrong > 5.0 or ghost key) rejected
  await assertFails(
    aliceDb.collection("mock_tests").doc("exam_rules_1").update({
      scoringRules: {
        marksPerCorrect: 1.0,
        negativeMarkPerWrong: 9.0,
        unansweredPenalty: 0.0,
        negativeMarkingEnabled: true,
        passingPercentage: 35.0,
      },
      updatedAt: pastDate(400),
    })
  );
  await assertFails(
    aliceDb.collection("mock_tests").doc("exam_rules_1").update({
      scoringRules: {
        marksPerCorrect: 1.0,
        negativeMarkPerWrong: 0.25,
        unansweredPenalty: 0.0,
        negativeMarkingEnabled: true,
        passingPercentage: 35.0,
        ghostBonus: 99,
      },
      updatedAt: pastDate(400),
    })
  );
});

test("Admins collection is never client-writable", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("admins").doc(ALICE_UID).set({ grantedAt: pastDate() }));
});
