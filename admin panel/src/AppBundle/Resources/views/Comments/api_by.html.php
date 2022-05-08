<?php 
$list=array();
foreach ($comments as $key => $comment) {
	$a["id"]=$comment->getId();
	$a["author"]=$comment->getUser()->getName();
	$a["content"]=$comment->getContent();
	$a["content"]=$comment->getContent();
	$a["enabled"]=$comment->getEnabled();
	$a["created"]=$view['time']->diff($comment->getCreated());
	$list[]=$a;
}
echo json_encode($list, JSON_UNESCAPED_UNICODE);
?>