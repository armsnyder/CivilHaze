package {
	import flash.events.TimerEvent;	
	import flash.utils.Timer;
	import flash.sampler.StackFrame;
	import flash.events.Event;
	import flash.display.MovieClip;
	
	public class DialogueBox extends MovieClip{
		
		private var timeBetweenLetters:int = 100;
		private var timeBetweenSentences:int = 200;
		private var timeBetweenPages:int = 2000;

		private var letterTimer:Timer = new Timer(timeBetweenLetters, 1);
		private var sentenceTimer:Timer = new Timer(timeBetweenSentences, 1);
		private var pageTimer:Timer = new Timer(timeBetweenPages, 1);

		private var pageQueue:Array = new Array();
		private var letterQueue:Array = new Array();
		
		private var dlgText:MovieClip = null;
		
		function DialogueBox() {
			this.addEventListener(Event.ADDED_TO_STAGE, addedToStage);
		}
		
		private function addedToStage(event:Event) {
			this.removeEventListener(Event.ADDED_TO_STAGE, addedToStage);
			dlgText = stage.getChildByName('dlgText') as MovieClip;
		}
		
		private function runLetter():void {
			letterTimer.removeEventListener(TimerEvent.TIMER, runLetter);
			sentenceTimer.removeEventListener(TimerEvent.TIMER, runLetter);
			if (letterQueue.length > 0) {
				var nextLetter:String = letterQueue.shift();
				dlgText.appendText(nextLetter);
				if ('.!?'.indexOf(nextLetter) >= 0) {
					sentenceTimer.addEventListener(TimerEvent.TIMER, runLetter);
					sentenceTimer.start();
				} else {
					letterTimer.addEventListener(TimerEvent.TIMER, runLetter);
					letterTimer.start();
				}
			} else {
				pageTimer.addEventListener(TimerEvent.TIMER, runPage);
			}
		}
		
		private function readPage(page:String):void {
			letterQueue = new Array();
			for (var i=0; i<page.length; i++) {
				letterQueue.push(page.substring(i, 1));
			}
		}

		private function runPage():void {
			if (pageTimer.hasEventListener(TimerEvent.TIMER)) {
				//pageTimer.removeEventListener(TimerEvent.TIMER, runPage);
			}
			if (pageQueue.length > 0) {
				//var nextPage:String = pageQueue.shift();
				//this.readPage(nextPage);
				//dlgText.text = '';
				//this.runLetter();
			}
		}

		public function runDialogueSequence(pages:Array):void {
			if (dlgText) {
				pageQueue = pages;
				this.runPage();
			}
		}
	}
}