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
