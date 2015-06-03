package
{
    import flash.net.URLLoader;
    import flash.net.URLRequest;
	import flash.events.*;
    import flash.net.URLRequestHeader;
    import flash.net.URLRequestMethod;
    import flash.net.URLVariables;

    public class EncapsulatedURLLoader
    {
        protected var _callback:Function;

        public function EncapsulatedURLLoader(url:String, callback:Function=null)
        {
            _callback = callback;
			var header:URLRequestHeader = new URLRequestHeader("pragma", "no-cache");
			var request:URLRequest = new URLRequest(url);
			var loader:URLLoader = new URLLoader();
			this.configureListeners(loader);
			//request.data = new URLVariables(variables);
			request.method = URLRequestMethod.GET;
			request.requestHeaders.push(header);
			try {
				loader.load(request);
			} catch (error:Error) {
				trace("Unable to load requested document.");
				//this.parent.removeChild(this);
			}
        }
		
		private function configureListeners(dispatcher:IEventDispatcher):void {
            dispatcher.addEventListener(Event.COMPLETE, completeHandler);
            dispatcher.addEventListener(Event.OPEN, openHandler);
            dispatcher.addEventListener(ProgressEvent.PROGRESS, progressHandler);
            dispatcher.addEventListener(SecurityErrorEvent.SECURITY_ERROR, securityErrorHandler);
            dispatcher.addEventListener(HTTPStatusEvent.HTTP_STATUS, httpStatusHandler);
            dispatcher.addEventListener(IOErrorEvent.IO_ERROR, ioErrorHandler);
        }

        private function completeHandler(event:Event):void {
            var loader:URLLoader = URLLoader(event.target);
			if (_callback != null) {
				_callback.call(null, loader.data);
			}
            //trace("completeHandler: " + loader.data);
			//this.parent.removeChild(this);
        }

        private function openHandler(event:Event):void {
            //trace("openHandler: " + event);
        }

        private function progressHandler(event:ProgressEvent):void {
            //trace("progressHandler loaded:" + event.bytesLoaded + " total: " + event.bytesTotal);
        }

        private function securityErrorHandler(event:SecurityErrorEvent):void {
            //trace("securityErrorHandler: " + event);
			//this.parent.removeChild(this);
        }

        private function httpStatusHandler(event:HTTPStatusEvent):void {
            //trace("httpStatusHandler: " + event);
        }

        private function ioErrorHandler(event:IOErrorEvent):void {
            //trace("ioErrorHandler: " + event);
			//this.parent.removeChild(this);
        }
    }
}