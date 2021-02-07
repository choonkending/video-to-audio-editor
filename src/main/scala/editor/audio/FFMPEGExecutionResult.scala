package editor.audio

sealed trait FFMPEGExecutionResult

case object FFMPEGExecutionSuccess extends FFMPEGExecutionResult
case object FFMPEGExecutionFailure extends FFMPEGExecutionResult
