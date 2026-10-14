
const API_URL = "http://localhost:8080/api";

// Mobile navigation
const menuToggle = document.getElementById("menuToggle");
const navLinks = document.querySelector(".nav-links");

if (menuToggle && navLinks) {
    menuToggle.addEventListener("click", () => {
        navLinks.classList.toggle("active");
    });

    navLinks.querySelectorAll("a").forEach((link) => {
        link.addEventListener("click", () => {
            navLinks.classList.remove("active");
        });
    });
}

// Check whether a student has logged in
function getLoggedInStudent() {
    const studentId = localStorage.getItem("studentId");
    const studentName = localStorage.getItem("studentName");

    if (!studentId || !studentName) {
        return null;
    }

    return { studentId, studentName };
}

// Display the student's homepage shortcuts
function updateStudentSection() {
    const student = getLoggedInStudent();

    const loginNotice = document.getElementById("courseLoginNotice");
    const courseTools = document.getElementById("courseTools");
    const studentShortcut = document.getElementById("studentShortcut");
    const welcomeStudentName = document.getElementById("welcomeStudentName");

    if (student) {
        if (loginNotice) loginNotice.classList.add("hidden");
        if (courseTools) courseTools.classList.remove("hidden");
        if (studentShortcut) studentShortcut.classList.remove("hidden");
        if (welcomeStudentName) {
            welcomeStudentName.textContent = student.studentName;
        }
    } else {
        if (loginNotice) loginNotice.classList.remove("hidden");
        if (courseTools) courseTools.classList.add("hidden");
        if (studentShortcut) studentShortcut.classList.add("hidden");
    }
}

// Show a message on the page
function showCourseMessage(message, type = "") {
    const messageElement = document.getElementById("courseMessage");

    if (!messageElement) return;

    messageElement.textContent = message;
    messageElement.className = "message";

    if (type) {
        messageElement.classList.add(type);
    }
}

// Load courses from Spring Boot
async function loadCourses(searchName = "") {
    const courseList = document.getElementById("courseList");

    if (!courseList) return;

    const student = getLoggedInStudent();

    if (!student) {
        courseList.innerHTML = "";
        return;
    }

    courseList.innerHTML = "";
    showCourseMessage("Loading courses...");

    try {
        let url = `${API_URL}/courses`;

        if (searchName.trim() !== "") {
            url = `${API_URL}/courses/search?name=${encodeURIComponent(searchName.trim())}`;
        }

        const response = await fetch(url);

        if (!response.ok) {
            throw new Error("Could not load courses.");
        }

        const courses = await response.json();

        if (!Array.isArray(courses) || courses.length === 0) {
            showCourseMessage("No courses found.");
            return;
        }

        showCourseMessage("");

        courses.forEach((course) => {
            const card = document.createElement("article");
            card.className = "course-card";

            const icon = document.createElement("div");
            icon.className = "course-icon";

            const iconElement = document.createElement("i");
            iconElement.className = "fa-solid fa-book-open";
            icon.appendChild(iconElement);

            const title = document.createElement("h3");
            title.textContent = course.courseName || "Course";

            const description = document.createElement("p");
            description.textContent =
                course.description || "Explore this course and start learning.";

            const fee = document.createElement("div");
            fee.className = "course-fee";
            fee.textContent = `₹${Number(course.fee || 0).toLocaleString("en-IN")}`;

            const button = document.createElement("button");
            button.className = "btn btn-primary";
            button.type = "button";
            button.textContent = "Enroll Now";

            button.addEventListener("click", () => {
                window.location.href = "student-dashboard.html";
            });

            card.append(icon, title, description, fee, button);
            courseList.appendChild(card);
        });
    } catch (error) {
        console.error(error);
        showCourseMessage(
            "Unable to load courses. Please check that Spring Boot is running.",
            "error"
        );
    }
}

// Search courses
const searchButton = document.getElementById("searchButton");
const viewAllButton = document.getElementById("viewAllButton");
const courseSearch = document.getElementById("courseSearch");

if (searchButton && courseSearch) {
    searchButton.addEventListener("click", () => {
        loadCourses(courseSearch.value);
    });
}

if (courseSearch) {
    courseSearch.addEventListener("keydown", (event) => {
        if (event.key === "Enter") {
            loadCourses(courseSearch.value);
        }
    });
}

if (viewAllButton && courseSearch) {
    viewAllButton.addEventListener("click", () => {
        courseSearch.value = "";
        loadCourses();
    });
}

// Initialize the homepage
updateStudentSection();

if (getLoggedInStudent()) {
    loadCourses();
}
