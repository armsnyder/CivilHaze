package {
	import flash.display.MovieClip;
	import DialogueBox;
	public class Main extends MovieClip {
		function Main() {
			gotoAndStop(1, 'introScene');
			var dialogueBox:DialogueBox = new DialogueBox();
			this.addChild(dialogueBox);
			dialogueBox.runDialogueSequence(["HELLO I AM AN OLD MAN.", "What can I do for you?"]);
		}
	}
}