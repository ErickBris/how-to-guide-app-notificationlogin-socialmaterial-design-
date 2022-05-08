<?php

namespace AppBundle\Controller;
use AppBundle\Entity\Guide;
use AppBundle\Entity\Step;
use MediaBundle\Entity\Media;
use AppBundle\Form\GuideType;
use AppBundle\Form\StepType;
use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpKernel\Exception\NotFoundHttpException;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\Serializer\Serializer;
use Symfony\Component\Serializer\Encoder\XmlEncoder;
use Symfony\Component\Serializer\Encoder\JsonEncoder;
use Symfony\Component\Serializer\Normalizer\ObjectNormalizer;
class GuidesController extends Controller
{
    public function api_getAction(Request $request,$id,$token)
    {
        
        if ($token!=$this->container->getParameter('token_app')) {
            throw new NotFoundHttpException("Page not found");  
        }
        $em=$this->getDoctrine()->getManager();
        $guide = $em->getRepository("AppBundle:Guide")->findOneBy(array("id"=>$id,"enabled"=>true));
        if ($guide==null) {
            throw new NotFoundHttpException("Page not found");   
        }
        return $this->render("AppBundle:Guides:get.html.php",array("guide"=>$guide));
    }
    public function api_nextAction(Request $request,$id,$token){
        if ($token!=$this->container->getParameter('token_app')) {
            throw new NotFoundHttpException("Page not found");  
        }
        $em=$this->getDoctrine()->getManager();
        $imagineCacheManager = $this->get('liip_imagine.cache.manager');

        $query = $em->createQuery(
           'SELECT a
            FROM AppBundle:Guide a
            WHERE a.enabled=true and a.id < '.$id
            .'ORDER BY a.created desc'
        )->setMaxResults(5);
        $guides = $query->getResult();
        return $this->render('AppBundle:Guides:api_all.html.php',array("guides"=>$guides));
    }
    public function api_allAction(Request $request,$token){
        if ($token!=$this->container->getParameter('token_app')) {
            throw new NotFoundHttpException("Page not found");  
        }
        $em=$this->getDoctrine()->getManager();
        $imagineCacheManager = $this->get('liip_imagine.cache.manager');

        $query = $em->createQuery(
           'SELECT g
            FROM AppBundle:Guide g
            WHERE g.enabled=true
            ORDER BY g.created desc'
        )->setMaxResults(5);
        $guides = $query->getResult();
        return $this->render('AppBundle:Guides:api_all.html.php',array("guides"=>$guides));
    }
    public function api_by_category_nextAction(Request $request,$category,$id,$token){
        if ($token!=$this->container->getParameter('token_app')) {
            throw new NotFoundHttpException("Page not found");  
        }
        $em=$this->getDoctrine()->getManager();
        $imagineCacheManager = $this->get('liip_imagine.cache.manager');

        $query = $em->createQuery(
           'SELECT a
            FROM AppBundle:Guide a
            WHERE a.enabled=true and a.category='.$category.' and a.id < '.$id
            .'ORDER BY a.created desc '
        )->setMaxResults(5);
        $guides = $query->getResult();
        return $this->render('AppBundle:Guides:api_all.html.php',array("guides"=>$guides));
    }
    public function api_by_queryAction(Request $request,$query,$token){
        if ($token!=$this->container->getParameter('token_app')) {
            throw new NotFoundHttpException("Page not found");  
        }
        $em=$this->getDoctrine()->getManager();
        $imagineCacheManager = $this->get('liip_imagine.cache.manager');
        $query = $em->createQuery(
           "SELECT a
            FROM AppBundle:Guide a
            WHERE a.enabled=true and a.title like '%".$query."%'"
            ." ORDER BY a.created desc "
        );
        $guides = $query->getResult();
        return $this->render('AppBundle:Guides:api_all.html.php',array("guides"=>$guides));
    }
    public function api_by_categoryAction(Request $request,$category,$token){
        if ($token!=$this->container->getParameter('token_app')) {
            throw new NotFoundHttpException("Page not found");  
        }
        $em=$this->getDoctrine()->getManager();
        $imagineCacheManager = $this->get('liip_imagine.cache.manager');
        $query = $em->createQuery(
           'SELECT a
            FROM AppBundle:Guide a
            WHERE a.enabled=true and a.category='.$category
            .'ORDER BY a.created desc '
        )->setMaxResults(5);
        $guides = $query->getResult();
        return $this->render('AppBundle:Guides:api_all.html.php',array("guides"=>$guides));
    }
    public function indexAction(Request $request)
    {
        $q="  ( 1=1 ) ";
        if ($request->query->has("title") and $request->query->get("title")!="") {
           $title=$request->query->get("title");
           $q.=" AND ( a.title like '%".$title."%')";
        }
        $em= $this->getDoctrine()->getManager();
        $dql        = "SELECT a FROM AppBundle:Guide a WHERE  ".$q. " ORDER BY a.created desc";
        $query      = $em->createQuery($dql);
        $paginator  = $this->get('knp_paginator');
        $pagination = $paginator->paginate(
        $query,
        $request->query->getInt('page', 1),
            12
        );
        return $this->render('AppBundle:Guides:index.html.twig',array("guides"=>$pagination));
    }
    public function addAction(Request $request)
    {
        $guide= new Guide();
        $form = $this->createForm(new GuideType(),$guide);
        $em=$this->getDoctrine()->getManager();
        $form->handleRequest($request);
        if ($form->isSubmitted() && $form->isValid()) {           
          if( $guide->getFile()!=null ){
                $media= new Media();
                $media->setFile($guide->getFile());
                $media->upload($this->container->getParameter('files_directory'));
                $media->setEnabled(true);
                $em->persist($media);
                $em->flush();
                $guide->setMedia($media);
                $em->persist($guide);
                $em->flush();
                $this->addFlash('success', 'Operation has been done successfully');
                return $this->redirect($this->generateUrl('app_guides_steps',array("id"=>$guide->getId())));
            }else{
                $error = new FormError("Required image file");
                $form->get('file')->addError($error);
            }
        }
        return $this->render("AppBundle:Guides:add.html.twig",array("form"=>$form->createView()));
    }
    
    public function edit_step_twoAction(Request $request,$id)
    {
        $em=$this->getDoctrine()->getManager();
        $step=$em->getRepository("AppBundle:Step")->find($id);
        if ($step==null) {
            throw new NotFoundHttpException("Page not found");
        }
        $form = $this->createForm(new StepType(),$step);
        $em=$this->getDoctrine()->getManager();
        $form->handleRequest($request);
        if ($form->isSubmitted() && $form->isValid()) {           
          if( $step->getFile()!=null ){
                $media_old=$step->getMedia();
                $media= new Media();
                $media->setFile($step->getFile());
                $media->upload($this->container->getParameter('files_directory'));
                $media->setEnabled(true);
                $em->persist($media);
                $em->flush();
                $step->setMedia($media);
                $em->flush();
                if($media_old!=null){
                    $media_old->delete($this->container->getParameter('files_directory'));
                    $em->remove($media_old);
                    $em->flush();
                }
                $this->addFlash('success', 'Operation has been done successfully');
                return $this->redirect($this->generateUrl('app_guides_steps',array("id"=>$step->getGuide()->getId())));
            }else{
                $em->flush();
                $this->addFlash('success', 'Operation has been done successfully');
                return $this->redirect($this->generateUrl('app_guides_steps',array("id"=>$step->getGuide()->getId())));
            }       
        }
        return $this->render("AppBundle:Guides:edit_step_two.html.twig",array("step"=>$step,"form"=>$form->createView()));
    }
    public function edit_stepAction(Request $request,$id)
    {
        $em=$this->getDoctrine()->getManager();
        $step=$em->getRepository("AppBundle:Step")->find($id);
        if ($step==null) {
            throw new NotFoundHttpException("Page not found");
        }
        $form = $this->createForm(new StepType(),$step);
        $em=$this->getDoctrine()->getManager();
        $form->handleRequest($request);
        if ($form->isSubmitted() && $form->isValid()) {           
          if( $step->getFile()!=null ){
                $media_old=$step->getMedia();
                $media= new Media();
                $media->setFile($step->getFile());
                $media->upload($this->container->getParameter('files_directory'));
                $media->setEnabled(true);
                $em->persist($media);
                $em->flush();
                $step->setMedia($media);
                $em->flush();
                if($media_old!=null){
                    $media_old->delete($this->container->getParameter('files_directory'));
                    $em->remove($media_old);
                    $em->flush();
                }
                $this->addFlash('success', 'Operation has been done successfully');
                return $this->redirect($this->generateUrl('app_guides_edit',array("id"=>$step->getGuide()->getId())));
            }else{
                $em->flush();
                $this->addFlash('success', 'Operation has been done successfully');
                return $this->redirect($this->generateUrl('app_guides_edit',array("id"=>$step->getGuide()->getId())));
            }       
        }
        return $this->render("AppBundle:Guides:edit_step.html.twig",array("step"=>$step,"form"=>$form->createView()));
    }
    public function stepsAction(Request $request,$id)
    {
        $em=$this->getDoctrine()->getManager();
        $guide=$em->getRepository("AppBundle:Guide")->find($id);
        if ($guide==null) {
            throw new NotFoundHttpException("Page not found");
        }
        $step=new Step();
        $form = $this->createForm(new StepType(),$step);
        $em=$this->getDoctrine()->getManager();
        $form->handleRequest($request);
        if ($form->isSubmitted() && $form->isValid()) {           
          if( $step->getFile()!=null ){
                $media= new Media();
                $media->setFile($step->getFile());
                $media->upload($this->container->getParameter('files_directory'));
                $media->setEnabled(true);
                $em->persist($media);
                $em->flush();
                $max=0;
                $steps=$em->getRepository('AppBundle:Step')->findBy(array('guide'=>$id));
                foreach ($steps as $key => $value) {
                    if ($value->getPosition()>$max) {
                        $max=$value->getPosition();
                    }
                }
                $step->setPosition($max+1);
                $step->setMedia($media);
                $step->setGuide($guide);
                $em->persist($step);
                $em->flush();
                $this->addFlash('success', 'Operation has been done successfully');
                return $this->redirect($this->generateUrl('app_guides_steps',array("id"=>$guide->getId())));
            }else{
                
                $max=0;
                $steps=$em->getRepository('AppBundle:Step')->findBy(array('guide'=>$id));
                foreach ($steps as $key => $value) {
                    if ($value->getPosition()>$max) {
                        $max=$value->getPosition();
                    }
                }
                $step->setPosition($max+1);
                $step->setGuide($guide);
                $em->persist($step);
                $em->flush();
                $this->addFlash('success', 'Operation has been done successfully');
                return $this->redirect($this->generateUrl('app_guides_steps',array("id"=>$guide->getId())));
            }       
        }
        return $this->render("AppBundle:Guides:steps.html.twig",array("guide"=>$guide,"form"=>$form->createView()));
    }
    public function finishAction(Request $request,$id)
    {
        $em=$this->getDoctrine()->getManager();
        $guide=$em->getRepository("AppBundle:Guide")->find($id);
        if ($guide==null) {
            throw new NotFoundHttpException("Page not found");
        }
        return $this->render("AppBundle:Guides:finish.html.twig",array("guide"=>$guide));
    }
    public function viewAction(Request $request,$id)
    {
        $em=$this->getDoctrine()->getManager();
        $guide=$em->getRepository("AppBundle:Guide")->find($id);
        if ($guide==null) {
            throw new NotFoundHttpException("Page not found");
        }
        $em= $this->getDoctrine()->getManager();
        $dql        = "SELECT c FROM AppBundle:Comment c WHERE c.guide =".$guide->getId();
        $query      = $em->createQuery($dql);
        $paginator  = $this->get('knp_paginator');
        $pagination = $paginator->paginate(
        $query,
        $request->query->getInt('page', 1),
            8
        );
        return $this->render("AppBundle:Guides:view.html.twig",array("guide"=>$guide,"pagination"=>$pagination));
    }
    public function delete_stepAction(Request $request,$id)
    {
        $em=$this->getDoctrine()->getManager();
        $step=$em->getRepository("AppBundle:Step")->find($id);
        if ($step==null) {
            throw new NotFoundHttpException("Page not found");
        }
        $guide=$step->getGuide();
        $media=$step->getMedia();
        $em->remove($step);
        $em->flush();
        if($media!=null){
            $media->delete($this->container->getParameter('files_directory'));
            $em->remove($media);
            $em->flush();
        }
        $p=1;
        foreach ($guide->getSteps() as $key => $value) {
                    $value->setPosition($p); 
                    $p++; 
         }
         $em->flush();
        $this->addFlash('success', 'Operation has been done successfully');
        return $this->redirect($this->generateUrl('app_guides_steps',array("id"=>$guide->getId())));
    }
    public function delete_step_editAction(Request $request,$id)
    {
        $em=$this->getDoctrine()->getManager();
        $step=$em->getRepository("AppBundle:Step")->find($id);
        if ($step==null) {
            throw new NotFoundHttpException("Page not found");
        }
        $guide=$step->getGuide();
        $media=$step->getMedia();
        $em->remove($step);
        $em->flush();
        if($media!=null){
            $media->delete($this->container->getParameter('files_directory'));
            $em->remove($media);
            $em->flush();
        }
        $p=1;
        foreach ($guide->getSteps() as $key => $value) {
                    $value->setPosition($p); 
                    $p++; 
         }
         $em->flush();
        $this->addFlash('success', 'Operation has been done successfully');
        return $this->redirect($this->generateUrl('app_guides_edit',array("id"=>$guide->getId())));
    }
    public function up_editAction(Request $request,$id)
    {
        $em=$this->getDoctrine()->getManager();
        $step=$em->getRepository("AppBundle:Step")->find($id);
        if ($step==null) {
            throw new NotFoundHttpException("Page not found");
        }
        if ($step->getPosition()>1) {
            $p=$step->getPosition();
            foreach ($step->getGuide()->getSteps() as $key => $value) {
                if ($value->getPosition()==$p-1) {
                    $value->setPosition($p);  
                }
            }
            $step->setPosition($step->getPosition()-1);
            $em->flush(); 
        }
       $this->addFlash('success', 'Operation has been done successfully');
        return $this->redirect($this->generateUrl('app_guides_edit',array("id"=>$step->getGuide()->getId())));
    }
    public function upAction(Request $request,$id)
    {
        $em=$this->getDoctrine()->getManager();
        $step=$em->getRepository("AppBundle:Step")->find($id);
        if ($step==null) {
            throw new NotFoundHttpException("Page not found");
        }
        if ($step->getPosition()>1) {
            $p=$step->getPosition();
            foreach ($step->getGuide()->getSteps() as $key => $value) {
                if ($value->getPosition()==$p-1) {
                    $value->setPosition($p);  
                }
            }
            $step->setPosition($step->getPosition()-1);
            $em->flush(); 
        }
       $this->addFlash('success', 'Operation has been done successfully');
        return $this->redirect($this->generateUrl('app_guides_steps',array("id"=>$step->getGuide()->getId())));
    }
    public function down_editAction(Request $request,$id)
    {
        $em=$this->getDoctrine()->getManager();
        $step=$em->getRepository("AppBundle:Step")->find($id);
        if ($step==null) {
            throw new NotFoundHttpException("Page not found");
        }
        $max=0;
        foreach ($step->getGuide()->getSteps() as $key => $value) {
            if ($max<$value->getPosition()) {
              $max=$value->getPosition();  
            }
        }
        if ($step->getPosition()<$max) {
            $p=$step->getPosition();
            foreach ($step->getGuide()->getSteps() as $key => $value) {
                if ($value->getPosition()==$p+1) {
                    $value->setPosition($p);  
                }
            }
            $step->setPosition($step->getPosition()+1);
            $em->flush();  
        }
        return $this->redirect($this->generateUrl('app_guides_edit',array("id"=>$step->getGuide()->getId())));
    }
    public function downAction(Request $request,$id)
    {
        $em=$this->getDoctrine()->getManager();
        $step=$em->getRepository("AppBundle:Step")->find($id);
        if ($step==null) {
            throw new NotFoundHttpException("Page not found");
        }
        $max=0;
        foreach ($step->getGuide()->getSteps() as $key => $value) {
            if ($max<$value->getPosition()) {
              $max=$value->getPosition();  
            }
        }
        if ($step->getPosition()<$max) {
            $p=$step->getPosition();
            foreach ($step->getGuide()->getSteps() as $key => $value) {
                if ($value->getPosition()==$p+1) {
                    $value->setPosition($p);  
                }
            }
            $step->setPosition($step->getPosition()+1);
            $em->flush();  
        }
        return $this->redirect($this->generateUrl('app_guides_steps',array("id"=>$step->getGuide()->getId())));
    }
    public function deleteAction($id,Request $request){
        $em=$this->getDoctrine()->getManager();
        $guide = $em->getRepository("AppBundle:Guide")->find($id);
        if($guide==null){
            throw new NotFoundHttpException("Page not found");
        }
        $form=$this->createFormBuilder(array('id' => $id))
            ->add('id', 'hidden')
            ->add('Yes', 'submit')
            ->getForm();
        $form->handleRequest($request);
        if($form->isSubmitted() && $form->isValid()) {
            foreach ($guide->getSteps() as $key => $step) {
                $media=$step->getMedia();
                $em->remove($step);
                $em->flush();
                if($media!=null){
                    $media->delete($this->container->getParameter('files_directory'));
                    $em->remove($media);
                    $em->flush();
                }
            }
            $media_guide=$guide->getMedia();
            $em->remove($guide);
            $em->flush();
            if($media_guide!=null){
                $media_guide->delete($this->container->getParameter('files_directory'));
                $em->remove($media_guide);
                $em->flush();
            }
            $this->addFlash('success', 'Operation has been done successfully');
            return $this->redirect($this->generateUrl('app_guides_index'));
        }
        return $this->render('AppBundle:Guides:delete.html.twig',array("form"=>$form->createView()));
    }
    public function editAction($id,Request $request){
        $em=$this->getDoctrine()->getManager();
        $guide = $em->getRepository("AppBundle:Guide")->find($id);
        if($guide==null){
            throw new NotFoundHttpException("Page not found");
        }



        $step_step=new Step();
        $form_step = $this->createForm(new StepType(),$step_step);
        $em_step=$this->getDoctrine()->getManager();
        $form_step->handleRequest($request);
        if ($form_step->isSubmitted() && $form_step->isValid()) {           
          if( $step_step->getFile()!=null ){
                $media_step= new Media();
                $media_step->setFile($step_step->getFile());
                $media_step->upload($this->container->getParameter('files_directory'));
                $media_step->setEnabled(true);
                $em_step->persist($media_step);
                $em_step->flush();
                $max_step=0;
                $steps_step=$em_step->getRepository('AppBundle:Step')->findBy(array('guide'=>$id));
                foreach ($steps_step as $key_step => $value_step) {
                    if ($value_step->getPosition()>$max_step) {
                        $max_step=$value_step->getPosition();
                    }
                }
                $step_step->setPosition($max_step+1);
                $step_step->setMedia($media_step);
                $step_step->setGuide($guide);
                $em_step->persist($step_step);
                $em_step->flush();
                $this->addFlash('success', 'Operation has been done successfully');
                //return $this->redirect($this->generateUrl('app_guides_steps',array("id"=>$guide->getId())));
                $step_step=new Step();
                $form_step = $this->createForm(new StepType(),$step_step);

            }else{
                
                $max_step=0;
                $steps_step=$em_step->getRepository('AppBundle:Step')->findBy(array('guide'=>$id));
                foreach ($steps_step as $key_step => $value_step) {
                    if ($value_step->getPosition()>$max_step) {
                        $max_step=$value_step->getPosition();
                    }
                }
                $step_step->setPosition($max_step+1);
                $step_step->setGuide($guide);
                $em_step->persist($step_step);
                $em_step->flush();
                $this->addFlash('success', 'Operation has been done successfully');
                //return $this->redirect($this->generateUrl('app_guides_steps',array("id"=>$guide->getId())));
                $step_step=new Step();
                $form_step = $this->createForm(new StepType(),$step_step);

            } 

        }





        $form = $this->createForm(new GuideType(),$guide);
        $em=$this->getDoctrine()->getManager();
        $form->handleRequest($request);
        if ($form->isSubmitted() && $form->isValid()) {           
          if( $guide->getFile()!=null ){
               $media_old=$guide->getMedia();
                $media= new Media();
                $media->setFile($guide->getFile());
                $media->upload($this->container->getParameter('files_directory'));
                $media->setEnabled(true);
                $em->persist($media);
                $em->flush();
                $guide->setMedia($media);
                $em->flush();
                if($media_old!=null){
                    $media_old->delete($this->container->getParameter('files_directory'));
                    $em->remove($media_old);
                    $em->flush();
                }
            }
            $em->flush();
            $this->addFlash('success', 'Operation has been done successfully');
            return $this->redirect($this->generateUrl('app_guides_index'));
        }
        return $this->render('AppBundle:Guides:edit.html.twig',array("guide"=>$guide,
            "form"=>$form->createView(),
            "form_step"=>$form_step->createView()
            ));
    }
    public function notifAction($id,Request $request){
        $em=$this->getDoctrine()->getManager();

        $guide = $em->getRepository("AppBundle:Guide")->findOneBy(array("id"=>$id,"enabled"=>true));
        if($guide==null){
            throw new NotFoundHttpException("Page not found");
        }
        $devices= $em->getRepository('AppBundle:Device')->findAll();
        $tokens=array();
        foreach ($devices as $key => $device) {
           $tokens[]=$device->getToken();
        }
        $this->addFlash('success', 'Operation has been done successfully');
        return $this->render("AppBundle:Guides:notif.html.php",array("guide"=>$guide,"tokens"=>$tokens));
    }
}