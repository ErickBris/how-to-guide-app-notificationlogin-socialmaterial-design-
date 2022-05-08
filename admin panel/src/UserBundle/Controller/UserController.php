<?php 
namespace UserBundle\Controller;
use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\HttpFoundation\Response;
use UserBundle\Entity\User;
use Symfony\Component\HttpKernel\Exception\NotFoundHttpException;
use Symfony\Component\HttpFoundation\Request;
use FOS\UserBundle\Form\Model\ChangePassword;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\Serializer\Serializer;
use Symfony\Component\Serializer\Encoder\XmlEncoder;
use Symfony\Component\Serializer\Encoder\JsonEncoder;
use Symfony\Component\Serializer\Normalizer\ObjectNormalizer;
use UserBundle\Form\UserType;
use MediaBundle\Entity\Media as Media;

class UserController extends Controller
{

    public function commentAction(Request $request,$id)
    {
        $em= $this->getDoctrine()->getManager();
        $user = $em->getRepository("UserBundle:User")->find($id);
        if ($user==null) {
            throw new NotFoundHttpException("Page not found");
        }
        if ($user->hasRole("ROLE_ADMIN")) {
            throw new NotFoundHttpException("Page not found");
        }

        $dql        = "SELECT c FROM AppBundle:Comment c  WHERE c.user = ".$user->getId();
        $query      = $em->createQuery($dql);
        $paginator  = $this->get('knp_paginator');
        $pagination = $paginator->paginate(
            $query,
            $request->query->getInt('page', 1),
            7
        );

        return $this->render(
                'UserBundle:User:comment.html.twig',array(
                    'pagination' => $pagination,
                    'user'=>$user
                )
                );
    }

    public function editAction(Request $request,$id)
    {
        $em= $this->getDoctrine()->getManager();
        $user = $em->getRepository("UserBundle:User")->find($id);
        if ($user==null) {
            throw new NotFoundHttpException("Page not found");
        }
        if ($user->hasRole("ROLE_ADMIN")) {
            throw new NotFoundHttpException("Page not found");
        }

        $dql        = "SELECT c FROM AppBundle:Comment c  WHERE c.user = ".$user->getId();
        $query      = $em->createQuery($dql);
        $paginator  = $this->get('knp_paginator');
        $pagination = $paginator->paginate(
            $query,
            $request->query->getInt('page', 1),
            7
        );
        $form = $this->createForm(new UserType(),$user);
        $form->handleRequest($request);
        if ($form->isSubmitted() && $form->isValid()) {
            $em->flush();
            $request->getSession()->getFlashBag()->add('success','Operation has been done successfully');
           return $this->redirect($this->generateUrl('user_user_index'));
        }
        return $this->render(
                'UserBundle:User:edit.html.twig',array(
                    "form"=>$form->createView(),
                    'pagination' => $pagination,
                    'user'=>$user
                )
                );
    }
    public function indexAction(Request $request)
    {
        $em= $this->getDoctrine()->getManager();
        $q=" AND ( 1=1 ) ";
        if ($request->query->has("q") and $request->query->get("q")!="") {
           $q.=" AND ( u.name like '%".$request->query->get("q")."%' or u.username like '%".$request->query->get("q")."%') ";
        }
        $dql        = "SELECT u FROM UserBundle:User u  WHERE (NOT u.roles LIKE '%ROLE_ADMIN%')   " .$q ." ";
        $query      = $em->createQuery($dql);
        $paginator  = $this->get('knp_paginator');
        $pagination = $paginator->paginate(
            $query,
            $request->query->getInt('page', 1),
            10
        );
        return $this->render("UserBundle:User:index.html.twig",array(
            'pagination' => $pagination,
        ));
    }
    public function api_loginAction($username,$password,$token)
    {
        if ($token!=$this->container->getParameter('token_app')) {
            throw new NotFoundHttpException("Page not found");  
        }
        $code="200";
        $message="";
        $errors=array();
        $em = $this->getDoctrine()->getManager();
        $user=$em->getRepository('UserBundle:User')->findOneBy(array("username"=>$username));

        if ($user) {
                  $encoder_service = $this->get('security.encoder_factory');
                  $encoder = $encoder_service->getEncoder($user);
                  if ($encoder->isPasswordValid($user->getPassword(), $password, $user->getSalt()) and !$user->hasRole("ROLE_ADMIN")) {
                    if ($user->isEnabled()==true) {
                        $code=200;  
                        $message="You have successfully logged in";
                        $errors[]=array("name"=>"id","value"=>$user->getId());
                        $errors[]=array("name"=>"name","value"=>$user->getName());
                        $errors[]=array("name"=>"type","value"=>$user->getType());
                        $errors[]=array("name"=>"username","value"=>$user->getUsername());
                        $errors[]=array("name"=>"salt","value"=>$user->getSalt());
                        $errors[]=array("name"=>"token","value"=>sha1($user->getPassword()));
                        $imagineCacheManager = $this->get('liip_imagine.cache.manager');
                        if ($user->getMedia()==null) {
                            $errors[]=array("name"=>"url","value"=>$imagineCacheManager->getBrowserPath("img/default_male.png", 'profile_picture'));        
                        }else{
                            $errors[]=array("name"=>"url","value"=>$imagineCacheManager->getBrowserPath($user->getMedia()->getLink(), 'profile_picture'));        
                        }
                    }else{
                        $message="Your account has been disabled by an administrator";
                        $code=500;
                    }
                  }else {
                        $code=500;  
                        $message="Invalid email address or password ";
                  }

        }else{
            $code=500;  
            $message="Invalid email address or password ";
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
    public function api_registerAction(Request $request,$token)
    {
        if ($token!=$this->container->getParameter('token_app')) {
            throw new NotFoundHttpException("Page not found");  
        }
    	$username=$request->get("username");
    	$password=$request->get("password");
    	$name=$request->get("name");
    	$type=$request->get("type");

    	$code="200";
    	$message="";
    	$errors=array();
    	$em = $this->getDoctrine()->getManager();
    	$u=$em->getRepository('UserBundle:User')->findOneByUsername($username);
    	if ($u!=null) {
    			if ($u->getType()=="email") {
    				$code=500;
    				$message="this email address already exists";
    				$errors[]=array("name"=>"username","value"=>"this email address already exists");
    			}else{
    				$code=200;
		    		$message="You have successfully logged in";
    				$errors[]=array("name"=>"id","value"=>$u->getId());
	                $errors[]=array("name"=>"name","value"=>$u->getName());
	                $errors[]=array("name"=>"username","value"=>$u->getUsername());
	                $errors[]=array("name"=>"salt","value"=>$u->getSalt());
	                $errors[]=array("name"=>"type","value"=>$u->getType());
	                $errors[]=array("name"=>"token","value"=>sha1($u->getPassword()));
                    $imagineCacheManager = $this->get('liip_imagine.cache.manager');
                    if ($u->getMedia()==null) {
                        $errors[]=array("name"=>"url","value"=>$imagineCacheManager->getBrowserPath("img/default_male.png", 'profile_picture'));        
                    }else{
                        $errors[]=array("name"=>"url","value"=>$imagineCacheManager->getBrowserPath($u->getMedia()->getLink(), 'profile_picture'));        
                    }
                }
    	}else{
	    	$user = new User();
			if (count($errors)==0) {
				$user->setUsername($username);
		    	$user->setPlainPassword($password);
		    	$user->setEmail($username);
		    	$user->setEnabled(true);
		    	$user->setName($name);
		    	$user->setType($type);
		    	$em->persist($user);
		    	$em->flush();
		    	$code=200;
		    	$message="You have successfully registered";
                $errors[]=array("name"=>"id","value"=>$user->getId());
                $errors[]=array("name"=>"name","value"=>$user->getName());
                $errors[]=array("name"=>"username","value"=>$user->getUsername());
                $errors[]=array("name"=>"salt","value"=>$user->getSalt());
                $errors[]=array("name"=>"type","value"=>$user->getType());
                $errors[]=array("name"=>"token","value"=>sha1($user->getPassword()));
		        $imagineCacheManager = $this->get('liip_imagine.cache.manager');
                if ($user->getMedia()==null) {
                    $errors[]=array("name"=>"url","value"=>$imagineCacheManager->getBrowserPath("img/default_male.png", 'profile_picture'));        
                }else{
                    $errors[]=array("name"=>"url","value"=>$imagineCacheManager->getBrowserPath($user->getMedia()->getLink(), 'profile_picture'));        
                }
            }else{
				$code=500;
    			$message="validation error";
			}
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
    public function api_change_passwordAction($id,$password,$new_password,$token)
    {
        if ($token!=$this->container->getParameter('token_app')) {
            throw new NotFoundHttpException("Page not found");  
        }
        sleep(2);
        $code="200";
        $message="";
        $errors=array();
        $em = $this->getDoctrine()->getManager();
        $user=$em->getRepository('UserBundle:User')->findOneBy(array("id"=>$id));  
        if ($user->hasRole("ROLE_ADMIN")) {
            throw new NotFoundHttpException("Page not found");
        }
        if ($user->getType()!="email") {
            throw new NotFoundHttpException("Page not found");
        }
        if ($user) {
            $encoder_service = $this->get('security.encoder_factory');
            $encoder = $encoder_service->getEncoder($user);
            if ($encoder->isPasswordValid($user->getPassword(), $password, $user->getSalt())) {

                if (strlen($new_password)<6) {
                    $code=500;
                    $errors["password"]="cette valeur est trop courte";
                }else{
                    $newPasswordEncoded = $encoder->encodePassword($new_password, $user->getSalt());
                    $user->setPassword($newPasswordEncoded);
                    $em->persist($user);
                    $em->flush();
                    $code=200;
                    $message="Password has been changed successfully";
                        $errors[]=array("name"=>"id","value"=>$user->getId());
                        $errors[]=array("name"=>"name","value"=>$user->getName());
                        $errors[]=array("name"=>"type","value"=>$user->getType());
                        $errors[]=array("name"=>"username","value"=>$user->getUsername());
                        $errors[]=array("name"=>"salt","value"=>$user->getSalt());
                        $errors[]=array("name"=>"token","value"=>sha1($user->getPassword()));
                }
            } else {
                $code=500;  
                $message="Current password is incorrect";
            }
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
    public function api_edit_nameAction($id,$name,$key,$token)
    {
        if ($token!=$this->container->getParameter('token_app')) {
            throw new NotFoundHttpException("Page not found");  
        }

        $code="200";
        $message="";
        $errors=array();
        $em = $this->getDoctrine()->getManager();
        $user=$em->getRepository('UserBundle:User')->findOneBy(array("id"=>$id));  
        if ($user->hasRole("ROLE_ADMIN")) {
            throw new NotFoundHttpException("Page not found");
        }
        if (sha1($user->getPassword()) != $key) {
            throw new NotFoundHttpException("Page not found");
        }
        if ($user) {
            $user->setName($name);
            $em->flush();
            $message="Your information has been edit ";
            $code="200";
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
    public function api_checkAction($id,$key,$token)
    {
        $code="500";
        $message="";
        $errors=array();
        if ($token!=$this->container->getParameter('token_app')) {
            $code=500;
        }

        $em = $this->getDoctrine()->getManager();
        $user=$em->getRepository('UserBundle:User')->findOneBy(array("id"=>$id));

        if($user){
            if ($user->isEnabled()) {
                if ($key==sha1($user->getPassword())) {
                    $code=200;
                }else{
                    $code=500;
                }
            }else{
                $code=500;
            }
            if ($user->hasRole("ROLE_ADMIN")) {
                $code=500;
            }
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
    public function api_uploadAction(Request $request,$id,$key,$token)
    {
        $code="200";
        $message="Ok";
        $values=array();
        if ($token!=$this->container->getParameter('token_app')) {
            throw new NotFoundHttpException("Page not found");  
        }
        $em=$this->getDoctrine()->getManager();
        $user=$em->getRepository('UserBundle:User')->findOneBy(array("id"=>$id));  
        if ($user->hasRole("ROLE_ADMIN")) {
            throw new NotFoundHttpException("Page not found");
        }
        if (sha1($user->getPassword()) != $key) {
            throw new NotFoundHttpException("Page not found");
        }
        if ($user) {        
            if ($this->getRequest()->files->has('uploaded_file')) {
                $old_media=$user->getMedia();
                $media= new Media();
                $media->setFile($this->getRequest()->files->get('uploaded_file'));
                $media->upload($this->container->getParameter('files_directory'));
                $media->setEnabled(true);
                $em->persist($media);
                $em->flush();
                $user->setMedia($media);
                if($old_media!=null){
                        $old_media->delete($this->container->getParameter('files_directory'));
                        $em->remove($old_media);
                        $em->flush();
                }
                $em->flush();   
                $imagineCacheManager = $this->get('liip_imagine.cache.manager');
                $values[]=array("name"=>"url","value"=>$imagineCacheManager->getBrowserPath($media->getLink(), 'profile_picture'));
            }
        }
        $error=array(
            "code"=>$code,
            "message"=>$message,
            "values"=>$values
            );
        $encoders = array(new XmlEncoder(), new JsonEncoder());
        $normalizers = array(new ObjectNormalizer());
        $serializer = new Serializer($normalizers, $encoders);
        $jsonContent=$serializer->serialize($error, 'json');
        return new Response($jsonContent);
    }
} 
