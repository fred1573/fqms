/**
 * 
 * @authors matelo
 * @date    2015-04-25 14:58:58
 * @version $Id$
 */

(function(win,doc){
     var timer;
     win.addEventListener('resize',function() {
      clearTimeout(timer);
      timer = setTimeout(setUnitA, 10);
      // location.reload()
    }, false);
    win.addEventListener('pageshow',function(e) {
        if (e.persisted) {
            clearTimeout(timer);
            timer = setTimeout(setUnitA, 10);
        }
    }, false);
    var setUnitA=function(){
        var clientWidth = doc.documentElement.clientWidth;
        var pageWidth = clientWidth > 640 ? 640 : clientWidth;
        doc.documentElement.style.fontSize = pageWidth * 0.9765625 + '%';
    };
    setUnitA();
})(window,document);