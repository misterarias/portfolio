(function () {

    var docElem = document.documentElement,
        header = $('header'),
        navbar = $('.navbar'),
        brand = $('.navbar-brand'),
        didScroll = false
        ;

    function init() {
        /**
         * Interesting Colon's egg problem here, if it starts hidden naturally fadeIn won't work
         */
        brand.removeClass("hidden");
        brand.hide();
        scrollPage();
        window.addEventListener('scroll', function (event) {
            if (!didScroll) {
                didScroll = true;
                setTimeout(scrollPage, 10);
            }
        }, false);
    }

    function scrollPage() {
        var sy = scrollY();
        var changeHeaderOn = (header.height() - navbar.height()*1.20 ); // 20% margin for great justice
        if (sy >= changeHeaderOn) {
            navbar.addClass('navbar-shrink');
            brand.fadeIn("slow");
        } else {
            navbar.removeClass('navbar-shrink');
            brand.fadeOut("fast");
        }
        didScroll = false;
    }

    function scrollY() {
        return window.pageYOffset || docElem.scrollTop;
    }

    init();

})();