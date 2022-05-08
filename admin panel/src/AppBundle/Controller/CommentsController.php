<?php

namespace AppBundle\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use AppBundle\Entity\Comment;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpKernel\Exception\NotFoundHttpException;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\Serializer\Serializer;
use Symfony\Component\Serializer\Encoder\XmlEncoder;
use Symfony\Component\Serializer\Encoder\JsonEncoder;
use Symfony\Component\Serializer\Normalizer\ObjectNormalizer;
use Symfony\Component\HttpFoundation\Response;

class CommentsController extends Controller
{
    public function indexAction(Request $request)
    {
        $em= $this->getDoctrine()->getManager();
        $dql        = "SELECT c FROM AppBundle:Comment c  ORDER BY c.created desc";
        $query      = $em->createQuery($dql);
        $paginator  = $this->get('knp_paginator');
        $pagination = $paginator->paginate(
        $query,
        $request->query->getInt('page', 1),
            10
        );
        return $this->render('AppBundle:Comments:index.html.twig',
            array(
                'pagination' => $pagination,
            )
        );

    }
    public function api_byAction($article,$token)
    {
        if ($token!=$this->container->getParameter('token_app')) {
            throw new NotFoundHttpException("Page not found");  
        }
        $em=$this->getDoctrine()->getManager();
        $a=$em->getRepository('AppBundle:Guide')->find($article);
        $comments=array();
        if ($a!=null) {
          $comments=$em->getRepository('AppBundle:Comment')->findBy(array("article"=>$a));
        }
        return $this->render('AppBundle:Comments:api_by.html.php',
            array(
                'comments' => $comments,
            )
        );  
    }
    public function api_addAction($user,$article,$comment,$token)
    {
        if ($token!=$this->container->getParameter('token_app')) {
            throw new NotFoundHttpException("Page not found");  
        }
        $em=$this->getDoctrine()->getManager();
        $a=$em->getRepository('AppBundle:Guide')->find($article);
        $u=$em->getRepository("UserBundle:User")->find($user);
        $code="200";
        $message="";
        $errors=array();

        $em=$this->getDoctrine()->getManager();
        $imagineCacheManager = $this->get('liip_imagine.cache.manager');


        if ($u!=null and $a!=null) {
            $comment_obj = new Comment();
            $comment_obj->setContent($comment);
            $comment_obj->setEnabled(true);
            $comment_obj->setGuide($a);
            $comment_obj->setUser($u);

            $em->persist($comment_obj);
            $em->flush();
            $errors[]=array("name"=>"id","value"=>$comment_obj->getId());
            $errors[]=array("name"=>"content","value"=>$comment_obj->getContent());
            if ($comment_obj->getUser()->getMedia()==null) {
                $errors[]=array("name"=>"image","value"=>$imagineCacheManager->getBrowserPath("img/default_male.png", 'profile_picture'));        
            }else{
                $errors[]=array("name"=>"image","value"=>$imagineCacheManager->getBrowserPath($comment_obj->getUser()->getMedia()->getLink(), 'profile_picture'));        
            }
            $errors[]=array("name"=>"author","value"=>$comment_obj->getUser()->getName());
            $errors[]=array("name"=>"enabled","value"=>$comment_obj->getEnabled());
            $errors[]=array("name"=>"created","value"=>"Now");
            $message="Your comment has been added";
        }else{
            $code="500";
            $message="Sorry, your comment could not be added at this time";
        }
        $error=array(
            "code"=>$code,
            "message"=>$message,
            "values"=>$errors
        );
        $encoders = array(new XmlEncoder(), new JsonEncoder());
        $normalizers = array(new ObjectNormalizer());
        $serializer = new Serializer($normalizers, $encoders);
        $jsonContent=$serializer->serialize($error, 'json');
        return new Response($jsonContent);
    }
    public function hideAction($id,Request $request){
        $em=$this->getDoctrine()->getManager();

        $comment = $em->getRepository("AppBundle:Comment")->find($id);
        if($comment==null){
            throw new NotFoundHttpException("Page not found");
        }
    	$guide=$comment->getGuide();
        $user=$comment->getUser();
    	if ($comment->getEnabled()==true) {
    		$comment->setEnabled(false);
    	}else{
    		$comment->setEnabled(true);
    	}
        $em->flush();
        $this->addFlash('success', 'Operation has been done successfully');
        if ($request->query->has("from") and $request->query->has("from")=="user") {
            return $this->redirect($this->generateUrl('user_user_edit',array("id"=>$user->getId())));
        }
        return $this->redirect($this->generateUrl('app_guides_view',array("id"=>$guide->getId())));

    }
    public function hide_twoAction($id,Request $request){
        $em=$this->getDoctrine()->getManager();

        $comment = $em->getRepository("AppBundle:Comment")->find($id);
        if($comment==null){
            throw new NotFoundHttpException("Page not found");
        }
        	$id_article=$comment->getGuide()->getId();
        	if ($comment->getEnabled()==true) {
        		$comment->setEnabled(false);
        	}else{
        		$comment->setEnabled(true);
        	}
            $em->flush();
            $this->addFlash('success', 'Operation has been done successfully');
            return $this->redirect($this->generateUrl('app_comments_index'));

    }
    public function delete_twoAction($id,Request $request){
        $em=$this->getDoctrine()->getManager();

        $comment = $em->getRepository("AppBundle:Comment")->find($id);
        if($comment==null){
            throw new NotFoundHttpException("Page not found");
        }
        $id_guide=$comment->getGuide()->getId();
        $form=$this->createFormBuilder(array('id' => $id))
            ->add('id', 'hidden')
            ->add('Yes', 'submit')
            ->getForm();
        $form->handleRequest($request);
        if($form->isSubmitted() && $form->isValid()) {
            $em->remove($comment);
            $em->flush();
            $this->addFlash('success', 'Operation has been done successfully');
            return $this->redirect($this->generateUrl('app_comments_index'));
        }
        return $this->render('AppBundle:Comments:delete.html.twig',array("form"=>$form->createView()));
    }
    public function deleteAction($id,Request $request){
        $em=$this->getDoctrine()->getManager();

        $comment = $em->getRepository("AppBundle:Comment")->find($id);
        if($comment==null){
            throw new NotFoundHttpException("Page not found");
        }
        $guide=$comment->getGuide();
        $user=$comment->getUser();
        $form=$this->createFormBuilder(array('id' => $id))
            ->add('id', 'hidden')
            ->add('Yes', 'submit')
            ->getForm();
        $form->handleRequest($request);
        if($form->isSubmitted() && $form->isValid()) {
            $em->remove($comment);
            $em->flush();
            $this->addFlash('success', 'Operation has been done successfully');
            if ($request->query->has("from") and $request->query->has("from")=="user") {
                return $this->redirect($this->generateUrl('user_user_edit',array("id"=>$user->getId())));
            }
            return $this->redirect($this->generateUrl('app_guides_view',array("id"=>$guide->getId())));

        }
        return $this->render('AppBundle:Comments:delete.html.twig',array("form"=>$form->createView()));
    }
}
