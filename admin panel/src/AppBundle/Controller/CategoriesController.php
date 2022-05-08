<?php

namespace AppBundle\Controller;
use AppBundle\Entity\Category;
use AppBundle\Form\CategoryType;
use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpKernel\Exception\NotFoundHttpException;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\Serializer\Serializer;
use Symfony\Component\Serializer\Encoder\XmlEncoder;
use Symfony\Component\Serializer\Encoder\JsonEncoder;
use Symfony\Component\Serializer\Normalizer\ObjectNormalizer;
class CategoriesController extends Controller
{



    public function api_listAction(Request $request,$token){
        if ($token!=$this->container->getParameter('token_app')) {
            throw new NotFoundHttpException("Page not found");  
        }
        $em=$this->getDoctrine()->getManager();
        $imagineCacheManager = $this->get('liip_imagine.cache.manager');
        $categories=$em->getRepository('AppBundle:Category')->findBy(array(),array("position"=>"asc"));
        

        $list=array();
        foreach ($categories as $key => $category) {
            $c["id"]=$category->getId();
            $c["title"]=$category->getTitle();
            $c["description"]=$category->getDescription();

            $list[]=$c;
        }
        header('Content-Type: application/json'); 
        $encoders = array(new XmlEncoder(), new JsonEncoder());
        $normalizers = array(new ObjectNormalizer());
        $serializer = new Serializer($normalizers, $encoders);
        $jsonContent=$serializer->serialize($list, 'json');
        return new Response($jsonContent);
    }

    public function indexAction(Request $request)
    {
	    $em=$this->getDoctrine()->getManager();
        $categories=$em->getRepository('AppBundle:Category')->findBy(array(),array("position"=>"asc"));
	    return $this->render('AppBundle:Categories:index.html.twig',array("categories"=>$categories));
    }
    public function addAction(Request $request)
    {
        $category= new Category();
        $form = $this->createForm(new CategoryType(),$category);
        $em=$this->getDoctrine()->getManager();
        $form->handleRequest($request);
        if ($form->isSubmitted() && $form->isValid()) {
            $max=0;
            $categories=$em->getRepository('AppBundle:Category')->findAll();
            foreach ($categories as $key => $value) {
                if ($value->getPosition()>$max) {
                    $max=$value->getPosition();
                }
            }
            $category->setPosition($max+1);
            $em->persist($category);
            $em->flush();
            $this->addFlash('success', 'operation has been done successfully');
            return $this->redirect($this->generateUrl('app_categories_index'));
        }
        return $this->render("AppBundle:Categories:add.html.twig",array("form"=>$form->createView()));
    }
    public function upAction(Request $request,$id)
    {
        $em=$this->getDoctrine()->getManager();
        $categorie=$em->getRepository("AppBundle:Category")->find($id);
        if ($categorie==null) {
            throw new NotFoundHttpException("Page not found");
        }
        if ($categorie->getPosition()>1) {
            $p=$categorie->getPosition();
            $categories=$em->getRepository('AppBundle:Category')->findAll();

            foreach ($categories as $key => $value) {
                if ($value->getPosition()==$p-1) {
                    $value->setPosition($p);  
                }
            }
            $categorie->setPosition($categorie->getPosition()-1);
            $em->flush(); 
        }
        return $this->redirect($this->generateUrl('app_categories_index'));
    }
    public function downAction(Request $request,$id)
    {
        $em=$this->getDoctrine()->getManager();
        $categorie=$em->getRepository("AppBundle:Category")->find($id);
        if ($categorie==null) {
            throw new NotFoundHttpException("Page not found");
        }
        $max=0;
        $categories=$em->getRepository('AppBundle:Category')->findAll();
        foreach ($categories  as $key => $value) {
            $max=$value->getPosition();  
        }
        if ($categorie->getPosition()<$max) {
            $p=$categorie->getPosition();
            foreach ($categories as $key => $value) {
                if ($value->getPosition()==$p+1) {
                    $value->setPosition($p);  
                }
            }
            $categorie->setPosition($categorie->getPosition()+1);
            $em->flush();  
        }
        return $this->redirect($this->generateUrl('app_categories_index'));
    }
    public function deleteAction($id,Request $request){
        $em=$this->getDoctrine()->getManager();

        $category = $em->getRepository("AppBundle:Category")->find($id);
        if($category==null){
            throw new NotFoundHttpException("Page not found");
        }

        $form=$this->createFormBuilder(array('id' => $id))
            ->add('id', 'hidden')
            ->add('Yes', 'submit')
            ->getForm();
        $form->handleRequest($request);
        if($form->isSubmitted() && $form->isValid()) {
            $em->remove($category);
            $em->flush();
            $this->addFlash('success', 'Operation has been done successfully');
            return $this->redirect($this->generateUrl('app_categories_index'));
        }
        return $this->render('AppBundle:Categories:delete.html.twig',array("form"=>$form->createView()));
    }
    public function editAction(Request $request,$id)
    {
        $em=$this->getDoctrine()->getManager();
        $categorie=$em->getRepository("AppBundle:Category")->find($id);
        if ($categorie==null) {
            throw new NotFoundHttpException("Page not found");
        }
        $form = $this->createForm(new CategoryType(),$categorie);
        $form->handleRequest($request);
        if ($form->isSubmitted() && $form->isValid()) {
            $em->persist($categorie);
            $em->flush();
            $this->addFlash('success', 'Operation has been done successfully');
            return $this->redirect($this->generateUrl('app_categories_index'));
 
        }
        return $this->render("AppBundle:Categories:edit.html.twig",array("form"=>$form->createView()));
    }
}