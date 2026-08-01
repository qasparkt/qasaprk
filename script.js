/* ===========================
   QASpark Institute – script.js
   =========================== */

// ---- Navbar Scroll ----
const navbar = document.getElementById('navbar');
window.addEventListener('scroll', () => {
  navbar.classList.toggle('scrolled', window.scrollY > 50);
  document.getElementById('scroll-top-btn').classList.toggle('visible', window.scrollY > 400);
  updateActiveNav();
}, { passive: true });

// ---- Hamburger ----
const hamburgerBtn = document.getElementById('hamburger-btn');
const navLinks = document.getElementById('nav-links');
hamburgerBtn.addEventListener('click', () => {
  const isOpen = navLinks.classList.toggle('open');
  hamburgerBtn.setAttribute('aria-expanded', String(isOpen));
});
document.addEventListener('click', (e) => {
  if (!navbar.contains(e.target)) navLinks.classList.remove('open');
});
navLinks.querySelectorAll('.nav-link').forEach(link => {
  link.addEventListener('click', () => navLinks.classList.remove('open'));
});

// ---- Active Nav on Scroll (home page only) ----
const currentPage = window.location.pathname.split('/').pop() || 'index.html';
const isHomePage = currentPage === 'index.html' || currentPage === '';
const sections = document.querySelectorAll('section[id]');

function updateActiveNav() {
  if (!isHomePage) return; // Let static HTML class handle it on inner pages
  let current = '';
  sections.forEach(sec => {
    if (window.scrollY >= sec.offsetTop - 120) current = sec.id;
  });
  document.querySelectorAll('.nav-link').forEach(link => {
    link.classList.toggle('active', link.getAttribute('href') === '#' + current);
  });
}

// ---- Scroll Reveal ----
const revealObserver = new IntersectionObserver((entries) => {
  entries.forEach((entry, i) => {
    if (entry.isIntersecting) {
      // Stagger delay based on sibling index
      const siblings = [...entry.target.parentElement.children].filter(el => el.classList.contains('reveal'));
      const idx = siblings.indexOf(entry.target);
      entry.target.style.transitionDelay = `${idx * 80}ms`;
      entry.target.classList.add('visible');
      revealObserver.unobserve(entry.target);
    }
  });
}, { threshold: 0.1, rootMargin: '0px 0px -40px 0px' });

document.querySelectorAll('.reveal').forEach(el => revealObserver.observe(el));

// ---- Count-up Animation ----
const countObserver = new IntersectionObserver((entries) => {
  entries.forEach(entry => {
    if (entry.isIntersecting) {
      entry.target.querySelectorAll('[data-target]').forEach(el => {
        animateCount(el, parseInt(el.dataset.target, 10));
      });
      countObserver.unobserve(entry.target);
    }
  });
}, { threshold: 0.5 });

const heroStats = document.querySelector('.hero-stats');
if (heroStats) countObserver.observe(heroStats);

function animateCount(el, target) {
  let current = 0;
  const duration = 1800;
  const increment = target / (duration / 16);
  const timer = setInterval(() => {
    current += increment;
    if (current >= target) {
      el.textContent = target.toLocaleString('en-IN');
      clearInterval(timer);
    } else {
      el.textContent = Math.floor(current).toLocaleString('en-IN');
    }
  }, 16);
}

// ---- Curriculum Tabs ----
const tabBtns = document.querySelectorAll('.tab-btn');
const tabPanels = document.querySelectorAll('.tab-panel');

tabBtns.forEach(btn => {
  btn.addEventListener('click', () => {
    const target = btn.getAttribute('aria-controls');

    tabBtns.forEach(b => {
      b.classList.remove('active');
      b.setAttribute('aria-selected', 'false');
    });
    tabPanels.forEach(p => {
      p.classList.remove('active');
      p.hidden = true;
    });

    btn.classList.add('active');
    btn.setAttribute('aria-selected', 'true');
    const panel = document.getElementById(target);
    if (panel) {
      panel.classList.add('active');
      panel.hidden = false;
    }
  });
});

// ---- Contact Form ----
const contactForm = document.getElementById('contact-form');
const formSuccess = document.getElementById('form-success');
const submitBtn = document.getElementById('form-submit-btn');

// Show inline field error
function showFieldError(input, message) {
  clearFieldError(input);
  input.classList.add('field-error');
  const err = document.createElement('span');
  err.className = 'field-error-msg';
  err.textContent = message;
  err.setAttribute('role', 'alert');
  input.parentElement.appendChild(err);
}

// Clear inline field error
function clearFieldError(input) {
  input.classList.remove('field-error');
  const existing = input.parentElement.querySelector('.field-error-msg');
  if (existing) existing.remove();
}

// Validate a single field
function validateField(input) {
  const val = input.value.trim();
  if (input.required && !val) {
    showFieldError(input, `${input.labels?.[0]?.textContent?.replace(' *','') || 'This field'} is required.`);
    return false;
  }
  if (input.type === 'email' && val && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(val)) {
    showFieldError(input, 'Please enter a valid email address.');
    return false;
  }
  if (input.type === 'tel' && val && !/^[6-9]\d{9}$/.test(val.replace(/\s+/g, ''))) {
    showFieldError(input, 'Please enter a valid 10-digit Indian mobile number.');
    return false;
  }
  clearFieldError(input);
  return true;
}

// Real-time validation on blur
if (contactForm) {
  contactForm.querySelectorAll('input, select, textarea').forEach(field => {
    field.addEventListener('blur', () => validateField(field));
    field.addEventListener('input', () => { if (field.classList.contains('field-error')) validateField(field); });
  });

  contactForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    // Validate all required fields
    let isValid = true;
    contactForm.querySelectorAll('input[required], select[required], textarea[required]').forEach(field => {
      if (!validateField(field)) isValid = false;
    });

    // Also validate email and phone format if filled
    const emailInput = document.getElementById('email-input');
    const phoneInput = document.getElementById('phone-input');
    if (emailInput.value && !validateField(emailInput)) isValid = false;
    if (phoneInput.value && !validateField(phoneInput)) isValid = false;

    if (!isValid) {
      const firstError = contactForm.querySelector('.field-error');
      if (firstError) firstError.scrollIntoView({ behavior: 'smooth', block: 'center' });
      return;
    }

    // Gather form data
    const name    = document.getElementById('name-input').value.trim();
    const email   = document.getElementById('email-input').value.trim();
    const phone   = document.getElementById('phone-input').value.trim();
    const course  = document.getElementById('course-select');
    const courseText = course.options[course.selectedIndex].text || 'Not specified';
    const message = document.getElementById('message-input').value.trim();

    submitBtn.textContent = '⏳ Processing with Java Backend...';
    submitBtn.disabled = true;

    try {
      // POST to Java Spring Boot REST API
      const response = await fetch('/api/inquiry', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name, email, phone, course: courseText, message })
      });

      if (response.ok) {
        const data = await response.json();
        contactForm.querySelectorAll('input, select, textarea').forEach(el => {
          el.value = '';
          clearFieldError(el);
        });

        formSuccess.innerHTML = `
          <div class="success-icon">✅</div>
          <strong>Inquiry Saved in Java Backend!</strong>
          <p style="margin-top:6px; font-size:0.9rem;">Ref ID: <strong style="color:var(--orange);">${data.inquiryId}</strong></p>
          <p style="margin-top:4px; font-size:0.85rem; color:var(--muted);">${data.message}</p>
        `;
        formSuccess.hidden = false;
        submitBtn.textContent = '🚀 Send Inquiry';
        submitBtn.disabled = false;
        formSuccess.scrollIntoView({ behavior: 'smooth', block: 'center' });
        return;
      }
    } catch (err) {
      console.log('Java API offline or file:// protocol, falling back to mailto flow:', err);
    }

    // Fallback: Open mailto link if offline or static file mode
    const subject = encodeURIComponent(`QASpark Inquiry from ${name}`);
    const body = encodeURIComponent(
`Hello QASpark Team,

I am interested in enrolling in your courses. Please find my details below:

Name        : ${name}
Email       : ${email}
Phone       : ${phone}
Course      : ${courseText}
Message     : ${message || 'No additional message.'}

Thank you,
${name}`
    );

    const mailtoLink = `mailto:qasparktesting@gmail.com?subject=${subject}&body=${body}`;
    window.location.href = mailtoLink;

    setTimeout(() => {
      contactForm.querySelectorAll('input, select, textarea').forEach(el => {
        el.value = '';
        clearFieldError(el);
      });
      formSuccess.hidden = false;
      submitBtn.textContent = '🚀 Send Inquiry';
      submitBtn.disabled = false;
      formSuccess.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }, 1000);
  });
}

// ---- Scroll to Top ----
document.getElementById('scroll-top-btn').addEventListener('click', () => {
  window.scrollTo({ top: 0, behavior: 'smooth' });
});

// ---- Auto-select course from URL ?course=xxx ----
const courseSelect = document.getElementById('course-select');
if (courseSelect) {
  const params = new URLSearchParams(window.location.search);
  const preselect = params.get('course');
  if (preselect) {
    courseSelect.value = preselect;
    // Scroll to form after a brief delay
    setTimeout(() => {
      const form = document.getElementById('contact-form');
      if (form) form.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }, 600);
  }
}

// ---- User-Friendly Course Filter ----
const filterBtns = document.querySelectorAll('.filter-btn');
const courseCards = document.querySelectorAll('.course-card[data-category]');

if (filterBtns.length > 0) {
  filterBtns.forEach(btn => {
    btn.addEventListener('click', () => {
      const category = btn.getAttribute('data-filter');
      
      filterBtns.forEach(b => b.classList.remove('active'));
      btn.classList.add('active');

      courseCards.forEach(card => {
        if (category === 'all' || card.getAttribute('data-category') === category) {
          card.style.display = 'flex';
          card.style.opacity = '1';
          card.style.transform = 'none';
        } else {
          card.style.display = 'none';
        }
      });
    });
  });
}



// ---- Smooth Scroll for Anchors ----
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
  anchor.addEventListener('click', (e) => {
    const target = document.querySelector(anchor.getAttribute('href'));
    if (target) {
      e.preventDefault();
      const offset = 80;
      window.scrollTo({ top: target.offsetTop - offset, behavior: 'smooth' });
    }
  });
});

// ---- Floating Cards Subtle Mouse Parallax ----
const heroVisual = document.querySelector('.hero-visual');
if (heroVisual && window.matchMedia('(prefers-reduced-motion: no-preference)').matches) {
  document.addEventListener('mousemove', (e) => {
    const { clientX: x, clientY: y } = e;
    const cx = window.innerWidth / 2;
    const cy = window.innerHeight / 2;
    const dx = (x - cx) / cx;
    const dy = (y - cy) / cy;
    heroVisual.style.transform = `translate(${dx * 8}px, ${dy * 6}px)`;
  }, { passive: true });
}

// ---- User Review & Rating Submission System ----
const reviewForm = document.getElementById('review-form');
const reviewsGrid = document.getElementById('reviews-grid');
const starRatingInput = document.getElementById('star-rating-input');
let selectedRating = 5;

if (starRatingInput) {
  const stars = starRatingInput.querySelectorAll('.star-icon');
  stars.forEach(star => {
    star.addEventListener('click', () => {
      selectedRating = parseInt(star.getAttribute('data-value'), 10);
      stars.forEach(s => {
        const val = parseInt(s.getAttribute('data-value'), 10);
        s.classList.toggle('active', val <= selectedRating);
      });
      const label = document.getElementById('selected-rating-val');
      if (label) label.textContent = selectedRating;
    });
  });
}

function escapeHTML(str) {
  return String(str).replace(/[&<>'"]/g, 
    tag => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', "'": '&#39;', '"': '&quot;' }[tag] || tag)
  );
}

function createReviewCardHTML(name, role, rating, text) {
  const starsHTML = '&#9733;'.repeat(rating) + '&#9734;'.repeat(5 - rating);
  return `
    <article class="testimonial-card reveal visible" style="border: 1px solid var(--orange);">
      <div class="testimonial-stars" aria-label="${rating} out of 5 stars">${starsHTML}</div>
      <blockquote><p>"${escapeHTML(text)}"</p></blockquote>
      <div class="testimonial-author">
        <div class="author-avatar">${escapeHTML(name.charAt(0).toUpperCase())}</div>
        <div class="author-info">
          <strong>${escapeHTML(name)}</strong>
          <span>${escapeHTML(role || 'Verified Student')}</span>
        </div>
      </div>
    </article>`;
}

function loadSavedReviews() {
  if (!reviewsGrid) return;
  const saved = JSON.parse(localStorage.getItem('qaspark_reviews') || '[]');
  saved.reverse().forEach(rev => {
    const cardHTML = createReviewCardHTML(rev.name, rev.role, rev.rating, rev.reviewText);
    reviewsGrid.insertAdjacentHTML('afterbegin', cardHTML);
  });
}

loadSavedReviews();

if (reviewForm) {
  reviewForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const name = document.getElementById('reviewer-name').value.trim();
    const role = document.getElementById('reviewer-role').value.trim();
    const reviewText = document.getElementById('reviewer-text').value.trim();
    const btn = document.getElementById('review-submit-btn');

    if (!name || !reviewText) {
      alert('Please enter your name and review message.');
      return;
    }

    btn.textContent = '⏳ Publishing Review...';
    btn.disabled = true;

    const newReview = { name, role: role || 'Verified Student', rating: selectedRating, reviewText };

    try {
      await fetch('/api/reviews', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(newReview)
      });
    } catch (err) {
      console.log('Java API offline, saving locally:', err);
    }

    const saved = JSON.parse(localStorage.getItem('qaspark_reviews') || '[]');
    saved.push(newReview);
    localStorage.setItem('qaspark_reviews', JSON.stringify(saved));

    if (reviewsGrid) {
      const cardHTML = createReviewCardHTML(newReview.name, newReview.role, newReview.rating, newReview.reviewText);
      reviewsGrid.insertAdjacentHTML('afterbegin', cardHTML);
    }

    const successMsg = document.getElementById('review-success-msg');
    if (successMsg) {
      successMsg.hidden = false;
      successMsg.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }

    reviewForm.reset();
    btn.textContent = '🌟 Submit Review';
    btn.disabled = false;
  });
}

// ---- Real-World Projects Modal System ----
const PROJECT_DATA = {
  ecommerce: {
    icon: '🛒', title: 'E-Commerce Application', category: 'Web Testing',
    desc: 'Test a full-featured online shopping platform covering product listings, cart management, checkout flows, payment gateways, order tracking, coupon logic, and user account management. This project gives real-world exposure to complex UI flows and database-driven validations.',
    learn: ['Write end-to-end test cases for cart & checkout', 'Test payment gateway integrations (UPI, cards)', 'Validate product search, filters & sorting', 'Test coupon/discount boundary values', 'Perform regression testing post-release', 'API testing for order management endpoints'],
    skills: ['Selenium (Java)', 'Playwright', 'Postman', 'MySQL', 'TestNG', 'Cucumber BDD']
  },
  banking: {
    icon: '🏦', title: 'Banking Portal', category: 'Finance / FinTech',
    desc: 'Test critical banking features including account login, fund transfers (NEFT/IMPS/RTGS), transaction history, loan application workflows, statement generation, and regulatory compliance. Focus on security, boundary values, and negative scenarios with high data sensitivity.',
    learn: ['Test fund transfer flows end-to-end', 'Validate transaction history & statements', 'Security testing for login & sessions', 'Boundary value analysis on amounts/limits', 'Database validation for transaction records', 'Compliance and regulatory field validations'],
    skills: ['Selenium WebDriver', 'SQL Testing', 'Postman', 'Security Testing', 'RestAssured', 'TestNG']
  },
  healthcare: {
    icon: '🏥', title: 'Healthcare Management System', category: 'MedTech / HealthIT',
    desc: 'Test a patient management platform covering patient registration, appointment scheduling, electronic health records (EHR), lab reports, doctor-patient portal, prescriptions, and HIPAA compliance. Practice complex data validation on medical workflows.',
    learn: ['Test patient registration & profile management', 'Validate appointment scheduling & slots', 'Test EHR data entry and retrieval flows', 'Lab report upload and viewing validation', 'Role-based access: Doctor vs Patient vs Admin', 'HIPAA compliance and data privacy testing'],
    skills: ['Selenium (Java)', 'Postman', 'MySQL', 'HIPAA Compliance', 'TestNG', 'Jira']
  },
  mobile: {
    icon: '📱', title: 'Mobile Application Testing', category: 'Mobile / Android / iOS',
    desc: 'Test native and hybrid mobile apps on Android and iOS. Practice gesture testing, push notifications, offline mode, deep linking, device compatibility, battery performance, and app store readiness using Appium automation.',
    learn: ['Setup Appium & Android Studio environment', 'Write Appium + Java mobile test scripts', 'Test gestures: swipe, pinch, scroll, tap', 'Validate push notifications and deep links', 'Device compatibility & screen size testing', 'Test offline mode and network interruptions'],
    skills: ['Appium', 'Android Studio', 'Java', 'iOS XCUITest', 'Device Farm', 'TestNG']
  },
  api: {
    icon: '🌐', title: 'REST & GraphQL API Projects', category: 'Backend / API Testing',
    desc: 'Perform comprehensive API testing including CRUD operations, OAuth 2.0, JWT, response schema validation, rate limiting, error handling, and contract testing. Automate complete API suites using Postman collections and RestAssured with Java.',
    learn: ['Test CRUD REST APIs (GET, POST, PUT, DELETE)', 'Validate response body, headers & status codes', 'Automate tests using RestAssured + Java', 'OAuth 2.0 and JWT token-based auth testing', 'GraphQL query and mutation testing', 'API performance and rate limit testing'],
    skills: ['Postman', 'RestAssured (Java)', 'Newman', 'GraphQL', 'JWT / OAuth 2.0', 'Swagger/OpenAPI']
  },
  auth: {
    icon: '🔐', title: 'Login & Authentication Flows', category: 'Security / Auth Testing',
    desc: 'Test complete authentication modules: SSO, MFA, RBAC, session management, password reset flows, account lockout policies, and brute-force protection. Ensure only the right users access the right data.',
    learn: ['Test SSO login and redirect flows', 'Validate OTP-based MFA authentication', 'Role-based access: Admin vs User vs Viewer', 'Session timeout and token expiry testing', 'Password reset and account recovery flows', 'Account lockout after failed attempts'],
    skills: ['Selenium', 'Postman', 'SSO Testing', 'Security Testing', 'OWASP Guidelines', 'RestAssured']
  },
  logistics: {
    icon: '🚛', title: 'Logistics Management System', category: 'Supply Chain / ERP',
    desc: 'Test end-to-end logistics workflows: shipment creation and tracking, warehouse management, route optimization, delivery scheduling, invoicing, carrier API integrations, and real-time GPS tracking. Validate data sync between warehouses, drivers, and customer portals.',
    learn: ['Test shipment creation and tracking flows', 'Validate warehouse inventory management', 'API testing for carrier integrations (FedEx, DHL)', 'Test route optimization algorithm outputs', 'Delivery status and notification testing', 'Invoice generation and billing validation'],
    skills: ['Selenium', 'Postman', 'SQL', 'RestAssured', 'API Automation', 'Regression Testing']
  },
  fitness: {
    icon: '🏋️', title: 'Fitness & Wellness Application', category: 'Health Tech / Consumer App',
    desc: 'Test a fitness app covering workout builders, calorie trackers, progress dashboards, wearable sync (Fitbit, Apple Watch, Google Fit), subscription billing, live class streaming, push notifications, and social sharing features.',
    learn: ['Test workout creation and tracking modules', 'Validate calorie and nutrition calculations', 'Wearable device data sync accuracy testing', 'Subscription and payment flow testing', 'Live streaming quality and fallback testing', 'Push notification delivery and scheduling'],
    skills: ['Appium', 'Selenium', 'Postman', 'Wearable API Testing', 'Subscription QA', 'Performance Testing']
  },
  messaging: {
    icon: '💬', title: 'Real-Time Messaging Platform', category: 'Communication / Real-Time',
    desc: 'Test a chat platform with WebSocket real-time messaging, group chats, media uploads (images, video, files), read receipts, typing indicators, end-to-end encryption, user blocking/reporting, and message search functionality.',
    learn: ['Test real-time messages via WebSockets', 'Validate message delivery and read receipts', 'Test media uploads: images, video, documents', 'End-to-end encryption validation', 'Group chat creation and admin controls', 'Load testing: concurrent users & message volume'],
    skills: ['WebSocket Testing', 'Postman', 'JMeter (Load Testing)', 'Selenium', 'Encryption QA', 'RestAssured']
  },
  game: {
    icon: '🎮', title: 'Game Testing (Web & Mobile)', category: 'Gaming / QA',
    desc: 'Test web and mobile games covering gameplay mechanics, level progression, in-app purchases (IAP), multiplayer sync, leaderboard accuracy, physics boundary testing, performance under load, and cross-platform compatibility.',
    learn: ['Test game level progression & unlock logic', 'Validate in-app purchases (coins, gems, skins)', 'Multiplayer sync and latency testing', 'Leaderboard accuracy and ranking validation', 'Cross-platform compatibility: PC, Android, iOS', 'Performance and FPS testing under load'],
    skills: ['Appium', 'Selenium', 'Gameplay Testing', 'IAP Testing', 'JMeter', 'Cross-Platform QA']
  },
  n8n: {
    icon: '⚡', title: 'n8n Workflow Automation Testing', category: 'No-Code / AI Automation',
    desc: 'Test n8n business automation workflows. Validate trigger conditions, node execution order, data transformation, API integrations (Slack, Gmail, Webhooks), error handling and retry logic, and complete end-to-end pipeline regression testing.',
    learn: ['Test workflow trigger conditions (Webhook, CRON)', 'Validate node execution sequence and data flow', 'Test integrations: Slack, Gmail, HTTP nodes', 'Error handling and retry mechanism validation', 'Data transformation and mapping accuracy', 'End-to-end pipeline regression via API'],
    skills: ['n8n Platform', 'API Testing', 'Postman', 'Webhook Testing', 'Integration QA', 'RestAssured']
  },
  ehs: {
    icon: '🏗️', title: 'Environment, Health & Safety (EHS)', category: 'EHS / Industrial Compliance',
    desc: 'Test an EHS compliance system including incident reporting, risk assessment, compliance dashboards, audit trail logging, OSHA/ISO report exports, employee safety training tracking, and automated notification escalation.',
    learn: ['Test incident report submission & approval', 'Validate risk assessment scoring and matrix', 'Audit trail and activity log verification', 'OSHA/ISO report generation and export testing', 'Employee training completion tracking', 'Notification escalation and alert testing'],
    skills: ['Selenium', 'Postman', 'SQL Validation', 'Compliance Testing', 'PDF/Export Testing', 'Regression Testing']
  }
};

const projectModal = document.getElementById('project-modal');
const modalCloseBtn = document.getElementById('modal-close-btn');

function openProjectModal(projectKey) {
  const data = PROJECT_DATA[projectKey];
  if (!data || !projectModal) return;

  document.getElementById('modal-icon').textContent = data.icon;
  document.getElementById('modal-title').textContent = data.title;
  document.getElementById('modal-category').textContent = data.category;
  document.getElementById('modal-desc').textContent = data.desc;

  const learnEl = document.getElementById('modal-learn');
  if (learnEl && data.learn) {
    learnEl.innerHTML = data.learn.map(item => `<li>${item}</li>`).join('');
  }

  const skillsEl = document.getElementById('modal-skills');
  if (skillsEl && data.skills) {
    skillsEl.innerHTML = data.skills.map(s => `<span>${s}</span>`).join('');
  }

  projectModal.hidden = false;
  document.body.style.overflow = 'hidden';
  if (modalCloseBtn) modalCloseBtn.focus();
}

function closeProjectModal() {
  if (!projectModal) return;
  projectModal.hidden = true;
  document.body.style.overflow = '';
}

document.querySelectorAll('.project-card-btn').forEach(btn => {
  btn.addEventListener('click', () => openProjectModal(btn.getAttribute('data-project')));
});

if (modalCloseBtn) modalCloseBtn.addEventListener('click', closeProjectModal);
if (projectModal) {
  projectModal.addEventListener('click', (e) => {
    if (e.target === projectModal) closeProjectModal();
  });
}
document.addEventListener('keydown', (e) => {
  if (e.key === 'Escape' && projectModal && !projectModal.hidden) closeProjectModal();
});

