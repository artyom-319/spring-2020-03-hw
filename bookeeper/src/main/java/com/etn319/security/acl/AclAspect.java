package com.etn319.security.acl;

import com.etn319.security.Roles;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@Aspect
@Component
public class AclAspect {
    private MutableAclService aclService;
    private IdentityExtractor identifierExtractor;
    private TransactionTemplate tt;

    public AclAspect(MutableAclService aclService, IdentityExtractor identityExtractor, TransactionTemplate tt) {
        this.aclService = aclService;
        this.identifierExtractor = identityExtractor;
        this.tt = tt;
    }

    @Around("@annotation(com.etn319.security.acl.CreateAcl)")
    public Object onCreate(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                Class<?> domainClass = result.getClass();
                Serializable domainId = identifierExtractor.extractId(result);
                // remove domainId
                ObjectIdentity identity = new ObjectIdentityImpl(domainClass, domainId);
                MutableAcl acl = aclService.createAcl(identity);
                Sid currentUser = currentUser();
                acl.setOwner(currentUser);
                acl.insertAce(0, BasePermission.WRITE, currentUser, true);
                acl.insertAce(0, BasePermission.DELETE, currentUser, true);
                acl.insertAce(0, BasePermission.ADMINISTRATION, admin(), true);
                aclService.updateAcl(acl);
            }
        });
        return result;
    }

    @AfterReturning(value = "@annotation(com.etn319.security.acl.DeleteAcl)")
    public void onDelete(JoinPoint joinPoint) {
        ObjectIdentity identity;
        Object domainObjectOrId = findObjectOrIdFromArgs(joinPoint);
        Method targetMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();
        DeleteAcl annotation = targetMethod.getAnnotation(DeleteAcl.class);
        if (annotation.byObjectId()) {
            Class<?> domainClass = annotation.aclClass();
            identity = new ObjectIdentityImpl(domainClass, (String) domainObjectOrId);
        } else {
            identity = new ObjectIdentityImpl(domainObjectOrId);
        }
        aclService.deleteAcl(identity, true);
    }

    private Sid currentUser() {
        return new PrincipalSid(SecurityContextHolder.getContext().getAuthentication());
    }

    private Sid admin() {
        return new GrantedAuthoritySid(Roles.ROLE_CAN_ADMINISTER);
    }

    private Object findObjectOrIdFromArgs(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length == 0)
            throw new RuntimeException("No arguments identifying object were provided to delete acl");
        if (args.length == 1)
            return args[0];
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method targetMethod = signature.getMethod();
        Annotation[][] parameterAnnotations = targetMethod.getParameterAnnotations();

        for (int argIndex = 0; argIndex < args.length; argIndex++) {
            for (Annotation ann : parameterAnnotations[argIndex]) {
                if (ann instanceof ObjectOrId)
                    return args[argIndex];
            }
        }
        throw new RuntimeException("More than 1 arg was found in DeleteAcl method and no @ObjectOrId was provided");
    }
}
