const openedMenu = document.querySelector('.opened-menu');
const closedMenu = document.querySelector('.closed-menu');
const navbarMenu = document.querySelector('.navbar');
const menuOverlay = document.querySelector('.overlay');

// Opened navbarMenu
// Closed navbarMenu
// Closed navbarMenu by Click Outside
openedMenu.addEventListener('click', toggleMenu);
closedMenu.addEventListener('click', toggleMenu);
menuOverlay.addEventListener('click', toggleMenu);

// Toggle Menu Function
function toggleMenu() {
   navbarMenu.classList.toggle('active');
   menuOverlay.classList.toggle('active');
   document.body.classList.toggle('scrolling');
}

navbarMenu.addEventListener('click', (event) => {
   if (event.target.hasAttribute('data-toggle') && window.innerWidth <= 992) {
      // Prevent Default Anchor Click Behavior
      event.preventDefault();
      const menuItemHasChildren = event.target.parentElement;

      // If menuItemHasChildren is Expanded, Collapse It
      if (menuItemHasChildren.classList.contains('active')) {
         collapseSubMenu();
      } else {
         // Collapse Existing Expanded menuItemHasChildren
         if (navbarMenu.querySelector('.menu-item-has-children.active')) {
            collapseSubMenu();
         }
         // Expand New menuItemHasChildren
         menuItemHasChildren.classList.add('active');
         const subMenu = menuItemHasChildren.querySelector('.sub-menu');
         subMenu.style.maxHeight = subMenu.scrollHeight + 'px';
      }
   }
});

// Collapse Sub Menu Function
function collapseSubMenu() {
   navbarMenu.querySelector('.menu-item-has-children.active .sub-menu').removeAttribute('style');
   navbarMenu.querySelector('.menu-item-has-children.active').classList.remove('active');
}

// Fixed Resize Screen Function
function resizeScreen() {
   // If navbarMenu is Open, Close It
   if (navbarMenu.classList.contains('active')) {
      toggleMenu();
   }

   // If menuItemHasChildren is Expanded, Collapse It
   if (navbarMenu.querySelector('.menu-item-has-children.active')) {
      collapseSubMenu();
   }
}

window.addEventListener('resize', () => {
   if (this.innerWidth > 992) {
      resizeScreen();
   }
});

/* Image Slider */
let i = 0; // current slide
let j = 4; // total slides

const dots = document.querySelectorAll(".dot-container button");
const images = document.querySelectorAll(".image-container img");

function next(){
    document.getElementById("content" + (i+1)).classList.remove("active");
    i = ( j + i + 1) % j;
    document.getElementById("content" + (i+1)).classList.add("active");
    indicator( i+ 1 );
}

function prev(){
    document.getElementById("content" + (i+1)).classList.remove("active");
    i = (j + i - 1) % j;
    document.getElementById("content" + (i+1)).classList.add("active");
    indicator(i+1);
}

function indicator(num){
    dots.forEach(function(dot){
        dot.style.backgroundColor = "transparent";
    });
    document.querySelector(".dot-container button:nth-child(" + num + ")").style.backgroundColor = "#8052ec";
}

function dot(index){
    images.forEach(function(image){
        image.classList.remove("active");
    });
    document.getElementById("content" + index).classList.add("active");
    i = index - 1;
    indicator(index);
}

function redirectUrl(num) {
   switch(num) {
      case "1":
         window.location.href = 'info';
         break;
      case "2":
         window.location.href = 'director';
         break;
      case "3":
         window.location.href = 'vision';
         break;
      case "4":
         window.location.href = 'road';
         break;
      case "5":
         window.location.href = 'notice';
         break;
      case "6":
         window.location.href = 'mainbusiness';
         break;
      case "7":
         window.location.href = 'resource';
         break;
   }
}

//다른 이름으로 파일 저장
const downloadAs = (url, name) => {
    axios.get(url, {
        headers: {
            "Content-Type": "application/octet-stream"
        },
        responseType: "blob"
    })
        .then(response => {
            const a = document.createElement("a");
            const url = window.URL.createObjectURL(response.data);
            a.href = url;
            a.download = name;
            a.click();
        })
        .catch(err => {
            console.log("error", err);
        });
};